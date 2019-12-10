import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"classpath:config.properties"})

public interface SimpleConfig extends Config {

    String email();
    @Key("password")
    String password();

    @Key("loginUrl")
    String loginUrl();
    @Key("userUrl")
    String userUrl();
}