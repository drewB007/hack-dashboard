package cap.ddg.hack.controller;

import cap.ddg.hack.model.*;
import cap.ddg.hack.service.*;

import com.revinate.guava.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
//import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
//import java.net.URI;
//import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.List;

@Controller
@PropertySource("classpath:application.properties")
public class EventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);


    @Value("${team.name}")
    private String thisTeam;

    @Value("${feature.one}")
    private String f1;

    @Value("${feature.two}")
    private String f2;

    private RateLimiter ratelimiter = null;

    @Value("${txn.limit}")
    private Double rateLimit;

    private void lazyLoadLimiter(){

        try{
            if(ratelimiter == null) {
                this.ratelimiter = RateLimiter.create(rateLimit);
                LOGGER.info("Created new rate limiter with rate: {}", ratelimiter.getRate());
            }
            else{
                LOGGER.info("Using existing rate limiter with rate: {}", ratelimiter.getRate());
            }
        }
        catch(Exception ex){
            this.ratelimiter = RateLimiter.create(2);
        }

    }

    final TeamService teamService;

    @Autowired
    EventController(TeamService service) {
        this.teamService = service;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root(Model model) {
        return "redirect:/home";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        return "redirect:/home";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String initHome(Model model) {
        String teamName = thisTeam;
        String feat2 = f2;

        model.addAttribute("teamName", teamName);
        model.addAttribute("featureTwo", feat2);

        String featureOne = f1;
        if(featureOne.equalsIgnoreCase("gotcha")){
            return "comp";
        }
        System.out.println("Looking for team : " + teamName);
        Team team = teamService.findById(teamName);

        model.addAttribute("votes", team.getVoteList());
        model.addAttribute("totalScore", team.getTotal());
        model.addAttribute("messages", team.getMessages());
        return "index";

    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminHome(Model model) {
        String teamName = "admin";

        model.addAttribute("teamName", teamName);

        List<Team> teamScores = new ArrayList<>(10);

        teamScores.add(teamService.findById("team1"));
        teamScores.add(teamService.findById("team2"));
        teamScores.add(teamService.findById("team3"));
        teamScores.add(teamService.findById("team4"));
        teamScores.add(teamService.findById("team5"));
        teamScores.add(teamService.findById("team6"));
        teamScores.add(teamService.findById("team7"));
        teamScores.add(teamService.findById("team8"));
        teamScores.add(teamService.findById("team9"));
        teamScores.add(teamService.findById("team10"));

        model.addAttribute("teamScores", teamScores);
        return "admin";

    }

    @RequestMapping(value = "/vote", method = RequestMethod.GET)
    public String getVote(Model model, Principal principal) {

        Team found = teamService.findById(thisTeam);

        System.out.println("Looking for: " + principal.getName()+ "  Found" + found);

        Vote vote = found.getVote(principal.getName());

        model.addAttribute(vote);
        return "vote";
    }


    @RequestMapping(value = "/vote", method = RequestMethod.POST)
    public String addVote(@Valid Vote vote, BindingResult result, SessionStatus status,
                             RedirectAttributes attributes, Principal principal) {

        System.out.println("called /vote for team " + thisTeam + " by " + principal.getName());

        if (result.hasErrors()) {
            return "vote";
        } else {
            if(principal.getName().equalsIgnoreCase(thisTeam) ){
                System.out.println("Self vote!!!");
                Message msg = new Message();
                msg.setSender("Admins");
                msg.setType("INFO");
                msg.setText("Nice try but you can't vote for yourself!");


                Team found = teamService.findById(thisTeam);
                System.out.println("Found team entry with information: " + found);

                found.addMessage(msg);
                Team updated = teamService.update(found);

                //update(found,thisTeam);



            }
            else {
                System.out.println(principal.getName() + " called POST: /vote with value: " + vote.getValue());
                status.setComplete();
                vote.setFromTeam(principal.getName());

                Team found = teamService.findById(thisTeam);

                found.addVote(vote);

                System.out.println("Added vote for: " + found);
                Message msg = new Message();
                msg.setSender(principal.getName());
                msg.setType("INFO");
                msg.setText(principal.getName() + " just cast a vote for you!");

                found.addMessage(msg);

                teamService.update(found);

            }

            return "redirect:/home";
//            return "redhome";
        }
    }

    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public String getEvent(Model model) {

        lazyLoadLimiter();
        ratelimiter.acquire();

//        String teamName = thisTeam;
//        EventDTO event = new EventDTO();
//        event.setTeam(teamName);
//        event.setEndpoint("/event");

        //Temp remove update to Mongo
        // EventDTO created = service.create(event);

        return "redirect:/home";
    }



    @RequestMapping(value = "/health", method = RequestMethod.GET)
    @ResponseBody
    public String getHealth() {

        System.out.println("health check ... OK");
        return "All Good Thanks";
    }


}
