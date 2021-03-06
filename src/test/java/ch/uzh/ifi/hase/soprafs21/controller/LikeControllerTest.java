package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.rest.dto.LikePostDTO;
import ch.uzh.ifi.hase.soprafs21.entity.Like;
import ch.uzh.ifi.hase.soprafs21.service.LikeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(LikeController.class)
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @Test
    public void testSwipe_validLikeRequest_successful() throws Exception {
        Like trueLike = new Like();
        trueLike.setItemIDSwiper((long) 1);
        trueLike.setItemIDSwiped((long) 2);
        trueLike.setLiked(true);

        LikePostDTO likePostDTO = new LikePostDTO();
        likePostDTO.setItemIDSwiper((long) 1);
        likePostDTO.setItemIDSwiped((long) 2);
        likePostDTO.setLiked(true);

        Mockito.when(likeService.createLike(trueLike)).thenReturn(trueLike);

        MockHttpServletRequestBuilder postRequest = post("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(likePostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isNoContent());


    }
    @Test
    public void testSwipe_invalidLikeRequest() throws Exception {
        Like like = new Like();
        like.setItemIDSwiper((long) 1);
        like.setItemIDSwiped((long) 2);
        like.setLiked(true);

        LikePostDTO likePostDTO = new LikePostDTO();
        likePostDTO.setItemIDSwiper((long) 1);
        likePostDTO.setItemIDSwiped((long) 2);
        likePostDTO.setLiked(null);

        Mockito.when(likeService.createLike(like)).thenReturn(like);

        MockHttpServletRequestBuilder postRequest = post("/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(likePostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());

    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"itemIDSwiper": "id1", "itemIDSwiped": "id2", "liked": "true/false"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}