package no.westerdals.westbook.rest;

import no.westerdals.westbook.MessageConstant;
import no.westerdals.westbook.model.Post;
import no.westerdals.westbook.mongodb.PostRepository;
import no.westerdals.westbook.responses.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import static no.westerdals.westbook.responses.ResultResponse.*;

import java.util.Date;
import java.util.List;

@RestController
public class PostRestController
{
    @Autowired
    private PostRepository postRepo;

    @RequestMapping(value = "/rest/v1/posts", method = RequestMethod.GET)
    public List<Post> getPosts(@RequestParam(defaultValue = "20")int maxPosts, @RequestParam(defaultValue = "0")int pageIndex)
    {
        if (maxPosts < 0 || pageIndex < 0)
            return null;
        return postRepo.findAll(new PageRequest(pageIndex, maxPosts)).getContent();
    }

    @RequestMapping(value = "/rest/v1/posts/{postId}", method = RequestMethod.GET)
    public Post getPost(@PathVariable String postId)
    {
        return postRepo.findOne(postId);
    }

    @RequestMapping(value = "/rest/v1/posts", method = RequestMethod.POST)
    public ResultResponse writePost(@RequestBody Post post)
    {
        post.setTime(new Date());
        Post result = postRepo.insert(post);
        return newOkResult(MessageConstant.POST_CREATED, result);
    }
}