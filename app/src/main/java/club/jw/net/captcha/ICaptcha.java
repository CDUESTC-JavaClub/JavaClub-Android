package club.jw.net.captcha;

import java.io.InputStream;

/**
 * 验证码校验器
 */
public interface ICaptcha {
    String ocr(InputStream image);
}
