package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Item;
import ch.uzh.ifi.hase.soprafs21.entity.Matches;
import ch.uzh.ifi.hase.soprafs21.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service ("matchService")
public class MatchService {
    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(@Qualifier ("matchRepository") MatchRepository matchRepository){
        this.matchRepository = matchRepository;
    }
    public void createMatch(long itemIdOne, long itemIdTwo){
        //check for existing match
        //actually this is redundant, because a match can only be created by two Likes, which are both tested for uniqueness already...
        for (Matches match: matchRepository.findAll()){
            if((match.getItemIdOne() == itemIdOne && match.getItemIdTwo() == itemIdTwo) ||
                match.getItemIdOne() == itemIdTwo && match.getItemIdTwo() == itemIdOne){
                return;
            }
        }
        //create new Match if it does not yet exist
        Matches newMatch = new Matches();
        newMatch.setItemIdOne(itemIdOne);
        newMatch.setItemIdTwo(itemIdTwo);

        //add Match to DB
        matchRepository.save(newMatch);
        matchRepository.flush();
    }
    public List<Matches> getAllMatches(){
        return matchRepository.findAll();
    }

    public List<Matches> getAllMatchesByItemID(long itemId){
        List<Matches> matchesByIdOne = matchRepository.findByItemIdOne(itemId);
        List<Matches> matchesByIdTwo = matchRepository.findByItemIdTwo(itemId);
        List<Matches> allMatchesById = new ArrayList<>();
        allMatchesById.addAll(matchesByIdOne);
        allMatchesById.addAll(matchesByIdTwo);
        return allMatchesById;
    }

    public List<Matches> getAllMatchesByItem(Item item){
        long itemId = item.getId();
        return getAllMatchesByItemID(itemId);
    }

    public Matches getMatchByMatchID(long matchID){
        return matchRepository.findById(matchID);
    }
}