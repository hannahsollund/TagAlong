package no.westerdals.tagalong.rest;

import no.westerdals.tagalong.model.Page;
import no.westerdals.tagalong.model.Tag;
import no.westerdals.tagalong.model.User;
import no.westerdals.tagalong.mongodb.PageRepository;
import no.westerdals.tagalong.mongodb.StudyFieldRepository;
import no.westerdals.tagalong.mongodb.TagRepository;
import no.westerdals.tagalong.mongodb.UserRepository;
import no.westerdals.tagalong.responses.ResolvedTag;
import no.westerdals.tagalong.responses.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/rest/v1/search")
@RestController
public class SearchRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private StudyFieldRepository studyFieldRepository;

    @RequestMapping(method=RequestMethod.GET)
    public List<SearchResult> searchAll(@RequestParam String query, @RequestParam(name="maxResults",defaultValue="20") int maxResults) {
        ArrayList<SearchResult> results = new ArrayList<>(searchUsers(query.split(" "), maxResults));
        results.addAll(searchPages(query, maxResults - results.size()));
        results.addAll(searchTags(query, maxResults - results.size()));
        results.addAll(searchStudyField(query, maxResults - results.size()));
        return results;
    }

    @RequestMapping(value="/users",method=RequestMethod.GET)
    public List<SearchResult<User>> searchUsers(@RequestParam String query[], @RequestParam(name="maxResults",defaultValue="20") int maxResults) {
        if (query.length > 4)
            return null;

        ArrayList<SearchResult<User>> found = new ArrayList<>();

        findUsersByFullName(found, query, maxResults);

        if (found.size() < maxResults) {
            findUsersBySurname(found, query, maxResults);
        }
        return found.stream().distinct().collect(Collectors.toList()); //TODO: Optimize
    }

    @RequestMapping(value="/pages",method=RequestMethod.GET)
    public List<SearchResult<Page>> searchPages(@RequestParam String query, @RequestParam(name="maxResults",defaultValue="20") int maxResults) {
        return pageRepository.findByNameLikeIgnoreCase(query, new PageRequest(0, maxResults))
                .stream()
                .map(page -> new SearchResult<>("page", page))
                .distinct()
                .collect(Collectors.toList());
    }

    //TODO: Optimize
    @RequestMapping(value="/tags",method=RequestMethod.GET)
    public List<SearchResult<ResolvedTag>> searchTags(@RequestParam String query, @RequestParam(name="maxResults",defaultValue="20") int maxResults) {
        return Stream.concat(tagRepository.findByNameLike(query, new PageRequest(0, maxResults)).stream(),
                tagRepository.findByDescription(query, new PageRequest(0, maxResults)).stream())
                .flatMap(this::resolveChildren)
                .map(this::resolve)
                .map(tag -> new SearchResult<>("tag", tag))
                .distinct()
                .collect(Collectors.toList());
    }

    @RequestMapping(value="/tagsold",method=RequestMethod.GET)
    public List<SearchResult<ResolvedTag>> searchTagsOld(@RequestParam String query, @RequestParam(name="maxResults",defaultValue="20") int maxResults) {
        return Stream.concat(tagRepository.findByNameLike(query, new PageRequest(0, maxResults)).stream(),
                tagRepository.findByDescription(query, new PageRequest(0, maxResults)).stream())
                .distinct()
                .map(this::resolve)
                .map(tag -> new SearchResult<>("tag", tag))
                .collect(Collectors.toList());
    }

    private List<SearchResult<User>> searchStudyField(@RequestParam String query, @RequestParam(name="maxResults",defaultValue="20") int maxResults) {
        return studyFieldRepository.getByNameIgnoreCaseLike(query)
                .stream()
                .flatMap(studyField -> userRepository.findByStudyFieldId(studyField.getId()).stream())
                .map(user -> new SearchResult<>("user", user))
                .collect(Collectors.toList());
    }

    private void findUsersByFullName(List<SearchResult<User>> found, String[] query, int maxResults) {
        if (query.length == 1) {
            List<User> users = userRepository.findByFirstnameLikeIgnoreCase(query[0], new PageRequest(0, maxResults));
            if (users != null) {
                users.stream().map(user -> new SearchResult<>("user", user))
                        .forEach(found::add);
            }
            return;
        }
        for (int i = 1; i < query.length; i++) {
            String possibleName = join(query, 0, i);
            String possibleSurname = join(query, i, query.length);
            List<User> users = userRepository.findByFirstnameLikeIgnoreCaseAndSurnameLikeIgnoreCase(possibleName, possibleSurname);
            if (users != null) {
                users.stream().map(user -> new SearchResult<>("user", user))
                        .forEach(found::add);
            }
            if (found.size() >= maxResults)
                return;
        }
    }

    private void findUsersBySurname(List<SearchResult<User>> found, String[] query, int maxResults) {
        userRepository.findBySurnameLikeIgnoreCase(query[0], new PageRequest(0, maxResults - found.size()))
                .stream()
                .map(user -> new SearchResult<>("user", user))
                .forEach(found::add);
    }

    private Stream<Tag> resolveChildren(Tag tag) {
        List<Tag> tags = tagRepository.findByParentId(tag.getId());
        tags.add(tag);
        return tags.stream();
    }

    private ResolvedTag resolve(Tag tag) {
        ResolvedTag resolvedTag = new ResolvedTag(tag.getId(), null, tag.getName(), tag.getDescription());
        if (tag.getParentId() == null) {
            return resolvedTag;
        }
        Tag parent = tagRepository.findOne(tag.getParentId());
        resolvedTag.setParent(resolve(parent));
        return resolvedTag;
    }

    private String join(String[] strs, int startIndex, int endIndex) {
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            result.append(strs[i]);
        }
        return result.toString();
    }
}
