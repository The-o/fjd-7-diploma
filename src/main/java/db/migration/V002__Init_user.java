package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class V002__Init_user extends BaseJavaMigration {

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    @Override
    public void migrate(Context context) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(PASSWORD);

        new JdbcTemplate(new SingleConnectionDataSource(context.getConnection(), true))
            .update("INSERT INTO app_user (login, password) VALUES (?, ?)", new Object[]{LOGIN, encodedPassword});
    }

}
