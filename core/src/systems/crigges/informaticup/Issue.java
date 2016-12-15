
package systems.crigges.informaticup;

import java.util.List;

public class Issue {

    public String url;
    public String repositoryUrl;
    public String labelsUrl;
    public String commentsUrl;
    public String eventsUrl;
    public String htmlUrl;
    public int id;
    public int number;
    public String title;
    public User user;
    public List<Object> labels = null;
    public String state;
    public boolean locked;
    public Assignee assignee;
    public List<Assignee_> assignees = null;
    public Object milestone;
    public int comments;
    public String createdAt;
    public String updatedAt;
    public Object closedAt;
    public String body;

}
