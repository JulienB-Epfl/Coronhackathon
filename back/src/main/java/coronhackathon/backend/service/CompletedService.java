package coronhackathon.backend.service;

import coronhackathon.backend.entity.Challenge;
import coronhackathon.backend.entity.HasCompleted;
import coronhackathon.backend.entity.User;
import coronhackathon.backend.repository.ChallengeRepository;
import coronhackathon.backend.repository.CompletedRepository;
import coronhackathon.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class CompletedService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private CompletedRepository completedRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    public long getNumberOfCompletedChallenges(long userId){
        return getCompletedChallenges(userId).size();
    }

    public List<Challenge> getCompletedChallenges(long userId) {
        List<Challenge> l = new ArrayList<Challenge>();
        Optional<User> ou = userRepository.findById(userId);
        if (!ou.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with id : " + userId + " not found");
        List<HasCompleted> lhc = completedRepository.findByUser(ou.get());
        for (HasCompleted hc : lhc) {
            l.add(hc.getChallenge());
        }
        return l;
    }

    public List<Challenge> getCompletedChallenges(String username) {
        List<Challenge> l = new ArrayList<Challenge>();
        Optional<User> ou = userRepository.findByUsername(username);
        if (!ou.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with name : " + username + " not found");
        List<HasCompleted> lhc = completedRepository.findByUser(ou.get());
        for (HasCompleted hc : lhc) {
            l.add(hc.getChallenge());
        }
        return l;
    }

    public List<User> getCompletersOfChallenge(long challengeId) {
        List<User> l = new ArrayList<User>();
        Optional<Challenge> oc = challengeRepository.findById(challengeId);
        if (!oc.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "challenge with id : " + challengeId + " not found");
        for (HasCompleted hc : completedRepository.findByChallenge(oc.get())) {
            l.add(hc.getUser());
        }
        return l;
    }

    /** Add a completed Challenge
     *
     * @param imgBase64 the image encoded in a base64 String
     * @param imgFormat jpg, png or jpeg
     * @return success message
     * @throws IOException
     */
    public String addCompletedChallenge(String username, long challengeId, String commentary, String imgBase64, String imgFormat)
    throws IOException {
        Optional<User> ou = userRepository.findByUsername(username);
        Optional<Challenge> oc = challengeRepository.findById(challengeId);
        if (!ou.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with name : " + username + " not found");
        if (!oc.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "challenge with id : " + challengeId + " not found");

        User user = ou.get();
        Challenge challenge = oc.get();

        if(completedRepository.findByUserAndChallenge(user, challenge).isPresent())
            return "User " + user.getUsername() + " has already completed " + challenge.getName();

        HasCompleted hc = new HasCompleted();
        hc.setChallenge(challenge);
        hc.setUser(user);
        hc.setCommentary(commentary);

        if(imgBase64.length() > 0) {

            Long id = userService.getUserByUsername(username).get().getId();

            String destinationPath = "resources/myCompletedImage/hasCompleted_"
                    + Long.toString(id)
                    + "_"
                    + Long.toString(challengeId) + "."+imgFormat;

            byte[] byteImg = Base64.getDecoder().decode(imgBase64);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(byteImg));
            ImageIO.write(img, "jpg", new File("src/main/" + destinationPath));
            hc.setPicture(destinationPath);
        }


        completedRepository.save(hc);
        return "User " + user.getUsername() + " has completed " + challenge.getName();

    }

    public List<Challenge> getCompletedChallengesByCategory(long userId, long categoryId) {
        List<Challenge> l = new ArrayList<>();
        Optional<User> ou = userRepository.findById(userId);
        if (!ou.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with id : " + userId + " not found");

        for (HasCompleted hc : completedRepository.findByUser(ou.get())) {
            if (hc.getChallenge().getCategoryId() == categoryId)
                l.add(hc.getChallenge());
        }
        return l;
    }


    public List<Challenge> getCompletedChallengesByCategory(String username, long categoryId) {
        List<Challenge> l = new ArrayList<>();
        Optional<User> ou = userRepository.findByUsername(username);
        if (!ou.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with name : " + username + " not found");

        for (HasCompleted hc : completedRepository.findByUser(ou.get())) {
            if (hc.getChallenge().getCategoryId() == categoryId)
                l.add(hc.getChallenge());
        }
        return l;
    }

    public void setPath(long userId, long challengeId, String destinationPath) {
        Optional<User> ou = userRepository.findById(userId);
        Optional<Challenge> oc = challengeRepository.findById(challengeId);
        if (!ou.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with id : " + userId + " not found");
        if (!oc.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "challenge with id : " + challengeId + " not found");

        User user = ou.get();
        Challenge challenge = oc.get();
        Optional<HasCompleted> ohc = completedRepository.findByUserAndChallenge(user, challenge);
        if (!ohc.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "has completed not found");

        else ohc.get().setPicture(destinationPath);
    }

    public List<Challenge> getCompletedChallengesByCategory(long userId, String name) {
        return getCompletedChallengesByCategory(userId, categoryService.getIdFromName(name));
    }

    public List<Challenge> getCompletedChallengesByCategory(String username, String name) {
        return getCompletedChallengesByCategory(username, categoryService.getIdFromName(name));
    }

    /**
     * return a list of (user, comment) pairs
     */
    public List<List<String>> getCommentsOfChallenge(long challengeId) {
        Optional<Challenge> oc = challengeRepository.findById(challengeId);
        if (!oc.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "challenge with id : " + challengeId + " not found");

        List<List<String>> userAndComments = new ArrayList<>();
        for (HasCompleted hc : completedRepository.findByChallenge(oc.get())) {

            List<String> UserAndComment = new ArrayList<String>();
            UserAndComment.add(hc.getUser().getUsername());
            UserAndComment.add(hc.getCommentary());

            userAndComments.add(UserAndComment);
        }

        return userAndComments;
    }
}
