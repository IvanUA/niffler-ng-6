package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.util.Optional;

import static guru.qa.niffler.data.Databases.*;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserJson createUser(UserJson user) {
        return transaction(
                connection -> {
                    UserEntity userEntity = UserEntity.fromJson(user);
                    return UserJson.fromEntity(
                            new UserdataUserDaoJdbc(connection).createUser(userEntity)
                    );
                },
                CFG.userdataJdbcUrl(),
                Connection.TRANSACTION_READ_COMMITTED
        );
    }

    public UserJson xaCreateUser(UserJson user) {
        return UserJson.fromEntity(
                xaTransaction(
                        Connection.TRANSACTION_REPEATABLE_READ,
                        new XaFunction<>(
                                connection -> {
                                    AuthUserEntity authUserEntity = new AuthUserEntity();
                                    authUserEntity.setUsername(user.username());
                                    authUserEntity.setPassword(pe.encode("12345"));
                                    authUserEntity.setEnabled(true);
                                    authUserEntity.setAccountNonExpired(true);
                                    authUserEntity.setAccountNonLocked(true);
                                    authUserEntity.setCredentialsNonExpired(true);
                                    new AuthUserDaoJdbc(connection).createUser(authUserEntity);

                                    AuthorityEntity[] authorities = new AuthorityEntity[Authority.values().length];
                                    for (int i = 0; i < Authority.values().length; i++) {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUserId(authUserEntity.getId());
                                        ae.setAuthority(Authority.values()[i]);
                                        authorities[i] = ae;
                                    }
                                    new AuthAuthorityDaoJdbc(connection).createAuthorities(authorities);

                                    return null;
                                },
                                CFG.authJdbcUrl()
                        ),
                        new XaFunction<>(
                                connection -> {
                                    UserEntity ue = new UserEntity();
                                    ue.setUsername(user.username());
                                    ue.setCurrency(user.currency());
                                    ue.setFirstname(user.firstname());
                                    ue.setSurname(user.surname());
                                    ue.setFullname(user.fullname());
                                    new UserdataUserDaoJdbc(connection).createUser(ue);
                                    return ue;
                                },
                                CFG.userdataJdbcUrl()
                        )
                )
        );
    }

    public Optional<UserJson> findUserByUsername(String username) {
        return transaction(
                connection -> {
                    return new UserdataUserDaoJdbc(connection).findByUsername(username).map(UserJson::fromEntity);
                },
                CFG.userdataJdbcUrl(),
                Connection.TRANSACTION_READ_COMMITTED
        );
    }

}
