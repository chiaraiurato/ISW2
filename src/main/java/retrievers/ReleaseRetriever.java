package retrievers;


import model.Release;
import org.json.JSONArray;
import org.json.JSONObject;
import utilities.CSV;
import utilities.JSON;

import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;

public class ReleaseRetriever {

    private final String projName ;

    public ReleaseRetriever(String projName){
        this.projName = projName;
    }

    public List<Release> getVersions() throws IOException, URISyntaxException {

        List<Release> releases = new ArrayList<>();
        int i;
        String url = "https://issues.apache.org/jira/rest/api/2/project/" + this.projName;
        JSONObject json = JSON.readJsonFromUrl(url);
        JSONArray versions = json.getJSONArray("versions");
        for (i = 0; i < versions.length(); i++ ) {
            String name = "";
            String id = "";
            if(versions.getJSONObject(i).has("releaseDate")) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                if (versions.getJSONObject(i).has("id"))
                    id = versions.getJSONObject(i).get("id").toString();
                String dateString = versions.getJSONObject(i).get("releaseDate").toString();
                releases.add(new Release(Integer.parseInt(id), name, LocalDate.parse(dateString)));
            }
        }
        releases.sort(Comparator.comparing(Release::getReleaseDate));
        setId(releases);
        CSV.createFileCSVForVersion(projName,releases);
        return releases;
    }
    private void setId(List<Release> releases){
        int i = 0;
        for (Release release : releases) {
            release.setId(++i);
        }
    }

}