package account.bootstrap;

import account.api.security.Role;
import account.domain.Group;
import account.domain.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DataLoader implements CommandLineRunner {

    private final GroupRepository groupRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createRoles();
    }

    private void createRoles() {
        if(Objects.isNull(groupRepository.findByAuthority(Role.getAuthorityNameByRole(Role.ADMINISTRATOR)))){
            groupRepository.save(new Group(Role.ADMINISTRATOR.name()));
            groupRepository.save(new Group(Role.ACCOUNTANT.name()));
            groupRepository.save(new Group(Role.USER.name()));
            groupRepository.save(new Group(Role.AUDITOR.name()));
        }
    }
}

