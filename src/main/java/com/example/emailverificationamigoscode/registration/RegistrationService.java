package com.example.emailverificationamigoscode.registration;

import com.example.emailverificationamigoscode.appuser.AppUser;
import com.example.emailverificationamigoscode.appuser.AppUserRole;
import com.example.emailverificationamigoscode.appuser.AppUserRoleRepository;
import com.example.emailverificationamigoscode.appuser.AppUserService;
import com.example.emailverificationamigoscode.email.EmailSender;
import com.example.emailverificationamigoscode.registration.token.ConfirmationToken;
import com.example.emailverificationamigoscode.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final AppUserRoleRepository appUserRoleRepository;
    private final static String USER_ROLE="USER";

    public String register(RegistrationRequest request) {
        AppUser appUser = new AppUser(request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword());
        Optional<AppUserRole> role= appUserRoleRepository.findByName(USER_ROLE);
        String token;
        if(role.isPresent()) {
            appUser.getAppUserRole().add(role.get());
             token = appUserService.singUpUser(appUser);
            String link = String.format("http://localhost:8080/api/v1/registration/confirm?token=%s", token);
            emailSender.send(request.getEmail(), buildEmail(request.getFirstName(), link));
        }
        else throw new NoSuchElementException("The role for user cannot be assigned");
        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }

    private String buildEmail(String name, String link) {
        return "<!doctype html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <title>Simple Transactional Email</title>\n" +
                "    <style>\n" +
                "@media only screen and (max-width: 620px) {\n" +
                "  table.body h1 {\n" +
                "    font-size: 28px !important;\n" +
                "    margin-bottom: 10px !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body p,\n" +
                "table.body ul,\n" +
                "table.body ol,\n" +
                "table.body td,\n" +
                "table.body span,\n" +
                "table.body a {\n" +
                "    font-size: 16px !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body .wrapper,\n" +
                "table.body .article {\n" +
                "    padding: 10px !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body .content {\n" +
                "    padding: 0 !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body .container {\n" +
                "    padding: 0 !important;\n" +
                "    width: 100% !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body .main {\n" +
                "    border-left-width: 0 !important;\n" +
                "    border-radius: 0 !important;\n" +
                "    border-right-width: 0 !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body .btn table {\n" +
                "    width: 100% !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body .btn a {\n" +
                "    width: 100% !important;\n" +
                "  }\n" +
                "\n" +
                "  table.body .img-responsive {\n" +
                "    height: auto !important;\n" +
                "    max-width: 100% !important;\n" +
                "    width: auto !important;\n" +
                "  }\n" +
                "}\n" +
                "@media all {\n" +
                "  .ExternalClass {\n" +
                "    width: 100%;\n" +
                "  }\n" +
                "\n" +
                "  .ExternalClass,\n" +
                ".ExternalClass p,\n" +
                ".ExternalClass span,\n" +
                ".ExternalClass font,\n" +
                ".ExternalClass td,\n" +
                ".ExternalClass div {\n" +
                "    line-height: 100%;\n" +
                "  }\n" +
                "\n" +
                "  .apple-link a {\n" +
                "    color: inherit !important;\n" +
                "    font-family: inherit !important;\n" +
                "    font-size: inherit !important;\n" +
                "    font-weight: inherit !important;\n" +
                "    line-height: inherit !important;\n" +
                "    text-decoration: none !important;\n" +
                "  }\n" +
                "\n" +
                "  #MessageViewBody a {\n" +
                "    color: inherit;\n" +
                "    text-decoration: none;\n" +
                "    font-size: inherit;\n" +
                "    font-family: inherit;\n" +
                "    font-weight: inherit;\n" +
                "    line-height: inherit;\n" +
                "  }\n" +
                "\n" +
                "  .btn-primary table td:hover {\n" +
                "    background-color: #34495e !important;\n" +
                "  }\n" +
                "\n" +
                "  .btn-primary a:hover {\n" +
                "    background-color: #34495e !important;\n" +
                "    border-color: #34495e !important;\n" +
                "  }\n" +
                "}\n" +
                "</style>\n" +
                "  </head>\n" +
                "  <body style=\"background-color: #f6f6f6; font-family: sans-serif; -webkit-font-smoothing: antialiased; font-size: 14px; line-height: 1.4; margin: 0; padding: 0; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">\n" +
                "    <span class=\"preheader\" style=\"color: transparent; display: none; height: 0; max-height: 0; max-width: 0; opacity: 0; overflow: hidden; mso-hide: all; visibility: hidden; width: 0;\">This is preheader text. Some clients will show this text as a preview.</span>\n" +
                "    <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f6f6f6; width: 100%;\" width=\"100%\" bgcolor=\"#f6f6f6\">\n" +
                "      <tr>\n" +
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>\n" +
                "        <td class=\"container\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; max-width: 580px; padding: 10px; width: 580px; margin: 0 auto;\" width=\"580\" valign=\"top\">\n" +
                "          <div class=\"content\" style=\"box-sizing: border-box; display: block; margin: 0 auto; max-width: 580px; padding: 10px;\">\n" +
                "\n" +
                "            <!-- START CENTERED WHITE CONTAINER -->\n" +
                "            <table role=\"presentation\" class=\"main\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background: #ffffff; border-radius: 3px; width: 100%;\" width=\"100%\">\n" +
                "\n" +
                "              <!-- START MAIN CONTENT AREA -->\n" +
                "              <tr>\n" +
                "                <td class=\"wrapper\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;\" valign=\"top\">\n" +
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\" width=\"100%\">\n" +
                "                    <tr>\n" +
                "                      <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">\n" +
                "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">Hi "+name+"</p>\n" +
                "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">To complete your registration you have to confirm you email. Click the link below to do that!</p>\n" +
                "                        <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; box-sizing: border-box; width: 100%;\" width=\"100%\">\n" +
                "                          <tbody>\n" +
                "                            <tr>\n" +
                "                              <td align=\"left\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; padding-bottom: 15px;\" valign=\"top\">\n" +
                "                                <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: auto;\">\n" +
                "                                  <tbody>\n" +
                "                                    <tr>\n" +
                "                                      <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; border-radius: 5px; text-align: center; background-color: #3498db;\" valign=\"top\" align=\"center\" bgcolor=\"#3498db\"> <a href="+link+" target=\"_blank\" style=\"border: solid 1px #3498db; border-radius: 5px; box-sizing: border-box; cursor: pointer; display: inline-block; font-size: 14px; font-weight: bold; margin: 0; padding: 12px 25px; text-decoration: none; text-transform: capitalize; background-color: #3498db; border-color: #3498db; color: #ffffff;\">Confirm your email</a> </td>\n" +
                "                                    </tr>\n" +
                "                                  </tbody>\n" +
                "                                </table>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </tbody>\n" +
                "                        </table>\n" +
                "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">We are really welcomed and pleased that you chose our company!</p>\n" +
                "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">Good luck, and don't forget to support us in our work!</p>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "\n" +
                "            <!-- END MAIN CONTENT AREA -->\n" +
                "            </table>\n" +
                "            <!-- END CENTERED WHITE CONTAINER -->\n" +
                "\n" +
                "            <!-- START FOOTER -->\n" +
                "            <div class=\"footer\" style=\"clear: both; margin-top: 10px; text-align: center; width: 100%;\">\n" +
                "              <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\" width=\"100%\">\n" +
                "                <tr>\n" +
                "                  <td class=\"content-block\" style=\"font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; color: #999999; font-size: 12px; text-align: center;\" valign=\"top\" align=\"center\">\n" +
                "                    <span class=\"apple-link\" style=\"color: #999999; font-size: 12px; text-align: center;\">Company Inc, 3 Abbey Road, San Francisco CA 94102</span>\n" +
                "                    <br> Don't like these emails? <a href=\"http://i.imgur.com/CScmqnj.gif\" style=\"text-decoration: underline; color: #999999; font-size: 12px; text-align: center;\">Unsubscribe</a>.\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                  <td class=\"content-block powered-by\" style=\"font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; color: #999999; font-size: 12px; text-align: center;\" valign=\"top\" align=\"center\">\n" +
                "                    Powered by <a href=\"http://htmlemail.io\" style=\"color: #999999; font-size: 12px; text-align: center; text-decoration: none;\">HTMLemail</a>.\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </div>\n" +
                "            <!-- END FOOTER -->\n" +
                "\n" +
                "          </div>\n" +
                "        </td>\n" +
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>\n";


//        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
//                "\n" +
//                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
//                "\n" +
//                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
//                "    <tbody><tr>\n" +
//                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
//                "        \n" +
//                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
//                "          <tbody><tr>\n" +
//                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
//                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
//                "                  <tbody><tr>\n" +
//                "                    <td style=\"padding-left:10px\">\n" +
//                "                  \n" +
//                "                    </td>\n" +
//                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
//                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
//                "                    </td>\n" +
//                "                  </tr>\n" +
//                "                </tbody></table>\n" +
//                "              </a>\n" +
//                "            </td>\n" +
//                "          </tr>\n" +
//                "        </tbody></table>\n" +
//                "        \n" +
//                "      </td>\n" +
//                "    </tr>\n" +
//                "  </tbody></table>\n" +
//                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
//                "    <tbody><tr>\n" +
//                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
//                "      <td>\n" +
//                "        \n" +
//                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
//                "                  <tbody><tr>\n" +
//                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
//                "                  </tr>\n" +
//                "                </tbody></table>\n" +
//                "        \n" +
//                "      </td>\n" +
//                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
//                "    </tr>\n" +
//                "  </tbody></table>\n" +
//                "\n" +
//                "\n" +
//                "\n" +
//                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
//                "    <tbody><tr>\n" +
//                "      <td height=\"30\"><br></td>\n" +
//                "    </tr>\n" +
//                "    <tr>\n" +
//                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
//                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
//                "        \n" +
//                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
//                "        \n" +
//                "      </td>\n" +
//                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
//                "    </tr>\n" +
//                "    <tr>\n" +
//                "      <td height=\"30\"><br></td>\n" +
//                "    </tr>\n" +
//                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
//                "\n" +
//                "</div></div>";
    }
}
