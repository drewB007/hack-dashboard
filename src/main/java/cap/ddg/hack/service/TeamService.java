package cap.ddg.hack.service;

import cap.ddg.hack.model.Team;

import java.util.List;

/**
 * Created by andrew on 2/22/17.
 */
public interface TeamService {


    Team create(Team team);

    Team update(Team team);

    Team delete(String id);

    List<Team> findAll();

    Team findById(String id);

}