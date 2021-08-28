package club.jw.net.entity.request;

import club.jw.net.anno.RequestParam;
import club.jw.net.enums.Language;

public class LoginRequest extends Request{
    @RequestParam("username")
    private String name;
    @RequestParam("password")
    private String password;
    @RequestParam("session_locale")
    private Language lang;

    public LoginRequest(String name, String password, Language lang) {
        this.name = name;
        this.password = password;
        this.lang = lang;
    }

    public LoginRequest name(String name){
        this.name = name;
        return this;
    }

    public LoginRequest password(String password){
        this.password = password;
        return this;
    }

    public LoginRequest lang(Language lang){
        this.lang = lang;
        return this;
    }

    public String getPassword() {
        return password;
    }
}
