package coronhackathon.backend.controller;

import coronhackathon.backend.entity.User;
import coronhackathon.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

@RestController
public class CompleteController {
    @Autowired
    private CompletedService completedService;
    @Autowired
    private UserService userService;

    /**
     * Marks user and challenge as completed.
     *
     * @param challengeId the completed challenge's Id
     * @param commentary  a commentary on the realisation of the challenge
     * @param imgBase64   image encoded in a base64 String
     * @param imgFormat jpg, png, jpeg...
     * @return a verification message
     */
    @PostMapping("/api/completeMyChallenge")
    public String completeChallenge(Principal principal, @RequestParam long challengeId,
                                    @RequestParam String commentary, @RequestParam String imgBase64, @RequestParam String imgFormat)
                                    throws IOException{
        return completedService.addCompletedChallenge(principal.getName(), challengeId, commentary, imgBase64, imgFormat);
    }

}
