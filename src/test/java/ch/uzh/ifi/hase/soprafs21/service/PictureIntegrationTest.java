package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Item;
import ch.uzh.ifi.hase.soprafs21.entity.Pictures;
import ch.uzh.ifi.hase.soprafs21.entity.Tags;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.ItemRepository;
import ch.uzh.ifi.hase.soprafs21.repository.PictureDBRepository;
import ch.uzh.ifi.hase.soprafs21.repository.TagsRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
@WebAppConfiguration
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PictureIntegrationTest {

    @Autowired
    private PictureDBRepository pictureDBRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private TagsRepository tagsRepository;


    @Autowired
    private PictureStorageService pictureStorageService;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;


    /*
     * THis test checks the upload of a picture -> Here we do not have an item, that goes with it, that is why
     * it will throw an error.
     */
    @Test
    public void upload_picture_validInput_noItem(){
        // Creates a Multipart File
        MockMultipartFile multiPFImage = new MockMultipartFile("abcpic.png", "abcpic.png",
       "image/png", "Generate bytes to simulate a picture".getBytes());

        Exception exception = assertThrows(ResponseStatusException.class, () -> pictureStorageService.uploadItemImage(1L, multiPFImage));
        assertEquals(exception.getMessage(),HttpStatus.CONFLICT+ " \"The item does not exist, thus you cannot add pictues!\"");

     }

     // Here we check, that only a image type is allowed and not a Filetype different to JPEG,PNG and GIF
    @Test
    public void upload_picture_invalidInput_wrongInputType(){
        // Creates a Mock Multipart File
        MockMultipartFile multiPFImage = new MockMultipartFile("abcpic.txt", "abcpic.txt",
                "text/plain", "Generate bytes to simulate a picture".getBytes());

        Exception exception = assertThrows(ResponseStatusException.class, () -> pictureStorageService.uploadItemImage(1L, multiPFImage));
        assertEquals(exception.getMessage(),HttpStatus.CONFLICT+ " \"Your filetype is not a JPEG,PNG or a GIF\"");

    }
    @Test
    public void upload_pictures_validInput_checkIfInDB_Integration() throws IOException {
        // Before:
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("1234");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        User savedUser = userRepository.save(user);

        // Create a Tag
        Tags tag = new Tags();
        tag.setDescription("Tag");
        tagsService.createTag(tag);
        List<Tags> tags = new ArrayList<>();
        tags.add(tag);

        // We create an item for the specific pictures
        Item item = new Item();
        item.setUserId(savedUser.getId());
        //item.setUserId(1L);
        item.setDescription("Description");
        item.setTitle("Title");
        item.setPicturecount(0);
        item.setItemtags(tags);
        Item repoItem = itemService.createItem(item);

        // We create 2 Mock images
        MockMultipartFile image1 = new MockMultipartFile("picture.png", "picture.png",
                "image/png", "Generate bytes to simulate a picture".getBytes());

        MockMultipartFile image2 = new MockMultipartFile("image.png", "image.png",
                "image/png", "Generate bytes to simusdssdlate a picture".getBytes());

        // We upload the two mock images (bypass AWS S3 Upload)
        pictureStorageService.uploadItemImage(repoItem.getId(),image1);
        pictureStorageService.uploadItemImage(repoItem.getId(),image2);

        // We test getAllPictures
        List<Pictures> pictures = new ArrayList<>();
        pictures = pictureStorageService.getAllPictures(repoItem.getId());

        // Tests if the pictures are in the Database
        assert(pictures.get(0).getName().contains("picture"));
        assert(pictures.get(1).getName().contains("image"));

    }
    }

