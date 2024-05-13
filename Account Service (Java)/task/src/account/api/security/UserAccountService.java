package account.api.security;

import account.AccountServiceApplication;
import account.api.admin.dto.UserLockUiDto;
import account.api.admin.dto.UserRoleUiDto;
import account.api.employee.dto.SuccessUiDto;
import account.api.security.dto.NewPasswordUiDto;
import account.api.security.dto.PasswordUpdatedUiDto;
import account.api.security.dto.SignupUiDto;
import account.api.security.dto.UserUiDto;
import account.domain.Group;
import account.domain.UserAccount;
import account.domain.repositories.GroupRepository;
import account.domain.repositories.UserAccountRepository;
import account.exception.AdminCannotBeLockedException;
import account.exception.AdminCannotDeleteThemselfOrAdminRoleCannotBeRemovedException;
import account.exception.AdministrativeAndBusinessRolesCannotBeCombinedException;
import account.exception.NewPasswordMustBeDifferentException;
import account.exception.PasswordBreachedException;
import account.exception.RoleNotFoundException;
import account.exception.UserDoesNotHaveRoleException;
import account.exception.UserExistsException;
import account.exception.UserHasOnlyOneRoleException;
import account.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final GroupRepository groupRepository;
    private final CustomBCryptPasswordEncoder encoder;


    public UserAccountService(UserAccountRepository userAccountRepository, GroupRepository groupRepository, CustomBCryptPasswordEncoder encoder) {
        this.userAccountRepository = userAccountRepository;
        this.groupRepository = groupRepository;
        this.encoder = encoder;
    }

    public List<UserUiDto> findAllUsers(){
        List<UserUiDto> userUiDtoList = new ArrayList<>();
        userAccountRepository.findAllByOrderByIdAsc().forEach(userAccount -> {
            UserUiDto userUiDto = new UserUiDto(userAccount.getId(), userAccount.getName(), userAccount.getLastname(), userAccount.getEmail(), userAccount.getRolesAsString());
            userUiDtoList.add(userUiDto);
        });
        return userUiDtoList;
    }

    @Transactional
    public UserUiDto addUser(SignupUiDto signupUiDto){

        String salt = BCrypt.gensalt();
        String hashedPassword = hashPassword(signupUiDto.getPassword(), salt);

        UserAccount userAccount = userAccountRepository.findByEmailEqualsIgnoreCase(signupUiDto.getEmail()).orElse(null);
        if(Objects.nonNull(userAccount)){
            throw new UserExistsException();
        }

        BreachedPasswordList breachedPasswordList = getBreachedPasswordList();
        if(breachedPasswordList.getBreachedPasswords().contains(signupUiDto.getPassword())){
            throw new PasswordBreachedException();
        }

        userAccount = new UserAccount();
        userAccount.setEmail(signupUiDto.getEmail().toLowerCase());
        userAccount.setName(signupUiDto.getName());
        userAccount.setLastname(signupUiDto.getLastname());
        userAccount.setPassword(hashedPassword);
        userAccount.setSalt(salt);

        List<Group> roles = new ArrayList<>();
        if (userAccountRepository.findFirstByAuthorities(groupRepository.findByAuthority(Role.getAuthorityNameByRole(Role.ADMINISTRATOR))).isPresent()) {
            roles.add(groupRepository.findByAuthority(Role.getAuthorityNameByRole(Role.USER)));
        } else {
            roles.add(groupRepository.findByAuthority(Role.getAuthorityNameByRole(Role.ADMINISTRATOR)));
        }
        userAccount.setAuthorities(roles);
        UserAccount savedUserAccount = userAccountRepository.save(userAccount);

        return new UserUiDto(savedUserAccount.getId(), savedUserAccount.getName(),
                savedUserAccount.getLastname(), savedUserAccount.getEmail(), savedUserAccount.getRolesAsString());
    }

    @Transactional
    public PasswordUpdatedUiDto updatePassword(NewPasswordUiDto newPasswordUiDto, Authentication authentication){
        UserAccount userAccount = userAccountRepository.findByEmailEqualsIgnoreCase(((CustomUserDetails) authentication.getPrincipal()).getUsername()).orElseThrow(() -> new IllegalStateException("User not found!"));
        if(checkNewPasswordIsTheSame(newPasswordUiDto.getNew_password(), userAccount.getPassword())){
            throw new NewPasswordMustBeDifferentException();
        }

        BreachedPasswordList breachedPasswordList = getBreachedPasswordList();
        if(breachedPasswordList.getBreachedPasswords().contains(newPasswordUiDto.getNew_password())){
            throw new PasswordBreachedException();
        }

        String salt = BCrypt.gensalt();
        String hashedPassword = hashPassword(newPasswordUiDto.getNew_password(), salt);

        userAccount.setPassword(hashedPassword);
        userAccount.setSalt(salt);
        UserAccount savedUserAccount = userAccountRepository.save(userAccount);

        return new PasswordUpdatedUiDto(savedUserAccount.getEmail());
    }

    private static BreachedPasswordList getBreachedPasswordList() {
        String jsonFilePath = "other/breached-passwords.json";

        BreachedPasswordList dataObject;
        try {
            InputStream inputStream = AccountServiceApplication.class.getClassLoader().getResourceAsStream(jsonFilePath);
            if (inputStream != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                dataObject = objectMapper.readValue(inputStream, BreachedPasswordList.class);
            } else {
                throw new IllegalStateException("BreachedPasswordList cannot be found!, inputStream is null");
            }
        } catch (IOException e) {
            throw new IllegalStateException("BreachedPasswordList cannot be found!, e:", e);
        }
        return dataObject;
    }

    private String hashPassword(String password, String salt) {
        return encoder.encode(password + salt);
    }

    private boolean checkNewPasswordIsTheSame(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }

    public UserAccount findByUsername(String username) {
        return userAccountRepository.findByEmailEqualsIgnoreCase(username).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public SuccessUiDto deleteUserAccount(String email, Authentication authentication) {
        UserAccount userAccount = findByUsername(email);
        if(((CustomUserDetails) authentication.getPrincipal()).getUsername().equals(email)){
            throw new AdminCannotDeleteThemselfOrAdminRoleCannotBeRemovedException();
        }
        userAccountRepository.deleteUserAccountByEmail(email);
        return new SuccessUiDto("Deleted successfully!", userAccount.getEmail(), userAccount.getId());
    }

    @Transactional
    public UserUiDto setUserAccountRoles(UserRoleUiDto userRoleUiDto) {
        UserAccount userAccount = userAccountRepository.findByEmailEqualsIgnoreCase(userRoleUiDto.getUser()).orElseThrow(UserNotFoundException::new);
        Group requiredAuthority = groupRepository.findByAuthority(Role.getAuthorityNameByRole(userRoleUiDto.getRole()));

        if(Objects.isNull(requiredAuthority)) throw new RoleNotFoundException();
        if (!userAccount.getAuthorities().contains(requiredAuthority) && userRoleUiDto.getOperation().equals(UserRoleUiDto.OperationType.REMOVE)){
            throw new UserDoesNotHaveRoleException();
        }
        if (userRoleUiDto.getRole().equals(Role.ADMINISTRATOR) && userRoleUiDto.getOperation().equals(UserRoleUiDto.OperationType.REMOVE)){
            throw new AdminCannotDeleteThemselfOrAdminRoleCannotBeRemovedException();
        }
        if (userAccount.getRoles().size() == 1 && userRoleUiDto.getOperation().equals(UserRoleUiDto.OperationType.REMOVE)){
            throw new UserHasOnlyOneRoleException();
        }
        administrativeAndBusinessRolesCannotBeMixedCheck(userRoleUiDto, userAccount);

        if (userRoleUiDto.getOperation().equals(UserRoleUiDto.OperationType.GRANT)){
            userAccount.addRole(requiredAuthority);
        } else {
            userAccount.removeRole(requiredAuthority);
        }
        userAccountRepository.save(userAccount);
        return new UserUiDto(userAccount.getId(), userAccount.getName(), userAccount.getLastname(), userAccount.getEmail(), userAccount.getRolesAsString());
    }

    private void administrativeAndBusinessRolesCannotBeMixedCheck(UserRoleUiDto userRoleUiDto, UserAccount userAccount) {
        if(userRoleUiDto.getOperation().equals(UserRoleUiDto.OperationType.GRANT)){
            if ((userRoleUiDto.getRole().equals(Role.ADMINISTRATOR) && (Role.getBusinessRoles().stream().anyMatch(role -> userAccount.getRoles().contains(role))))
                    || (Role.getBusinessRoles().stream().anyMatch(role -> userRoleUiDto.getRole().equals(role)) && userAccount.getRoles().contains(Role.ADMINISTRATOR))){
                throw new AdministrativeAndBusinessRolesCannotBeCombinedException();
            }
        }
    }

    @Transactional
    public SuccessUiDto accountLocking(UserLockUiDto userLockUiDto) {
        UserAccount userAccount = userAccountRepository.findByEmailEqualsIgnoreCase(userLockUiDto.getUser()).orElseThrow(UserNotFoundException::new);
        if(userAccount.getRoles().contains(Role.ADMINISTRATOR) && userLockUiDto.getOperation().equals(UserLockUiDto.OperationType.LOCK)){
            throw new AdminCannotBeLockedException();
        }
        if(userLockUiDto.getOperation().equals(UserLockUiDto.OperationType.UNLOCK)){
            userAccount.setFailedAttempt(0);
        }
        userAccount.setLocked(userLockUiDto.getOperation().equals(UserLockUiDto.OperationType.LOCK));
        userAccountRepository.save(userAccount);
        return new SuccessUiDto("User " + userLockUiDto.getUser() + " " + userLockUiDto.lockingString() + "!", userAccount.getEmail(), userAccount.getId());
    }

    @Transactional
    public SuccessUiDto updateAccountLocking(UserLockUiDto userLockUiDto) {
        UserAccount userAccount = userAccountRepository.findByEmailEqualsIgnoreCase(userLockUiDto.getUser()).orElseThrow(UserNotFoundException::new);
        if(userAccount.getRoles().contains(Role.ADMINISTRATOR) && userLockUiDto.getOperation().equals(UserLockUiDto.OperationType.LOCK)){
            throw new AdminCannotBeLockedException();
        }
        if(userLockUiDto.getOperation().equals(UserLockUiDto.OperationType.UNLOCK)){
            userAccount.setFailedAttempt(0);
        }
        userAccount.setLocked(userLockUiDto.getOperation().equals(UserLockUiDto.OperationType.LOCK));
        userAccountRepository.save(userAccount);
        return new SuccessUiDto("User " + userLockUiDto.getUser().toLowerCase() + " " + userLockUiDto.lockingString() + "!",null, userAccount.getId());
    }

    @Transactional
    public void saveUser(UserAccount userAccount) {
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public void increaseFailedAttempCount(UserAccount userAccount) {
        userAccount.setFailedAttempt(userAccount.getFailedAttempt() + 1);
        userAccountRepository.save(userAccount);
    }

    @Transactional
    public void resetFailedAttempCount(UserAccount userAccount) {
        userAccount.setFailedAttempt(0);
        userAccountRepository.save(userAccount);
    }
}
