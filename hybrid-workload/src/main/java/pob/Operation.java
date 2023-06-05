package pob;

import java.util.HashMap;

public class Operation {
    public String typeName;
    public int recordID;
    public long start;
    public long end;
    public long j;
    public String[] values;
    public HashMap<String, Object> mapping;

    public Operation(int j, String line) {
        this(j, line, null);
    }

    public Operation(String line) {
        this(0, line);
    }

    public Operation(int j, String line, String[] values) {
        this.j = j;
        recordID = 0;
        typeName = null;
        this.values = values;
        for (int i = 0; i < line.length(); ++i) {
            if (line.charAt(i) == '|') {
                typeName = line.substring(0, i);
            } else if (typeName != null) {
                recordID *= 10;
                recordID += line.charAt(i) - '0';
            }
        }
    }

    public void assignFindParameter() {
        mapping = new HashMap<>();
        switch (typeName) {
            case "findUser":
            case "findFollow":
            case "findSubscribe":
            case "findRate":
                mapping.put("user_id", recordID);
                break;
            case "findArtist":
                mapping.put("artist_id", recordID);
                break;
            case "findTrack":
                mapping.put("aria_id", recordID);
                break;
        }
    }

    public void assignValue(String value) {
        values = value.split(",");
        mapping = new HashMap<>();
        switch (typeName) {
            case "createArtistPrev":
                assert values.length == 6;
                mapping.put("_artist_id", values[0]);
                mapping.put("artist_id", Integer.valueOf(values[0]));
                mapping.put("name", values[1].substring(1, values[1].length() - 1));
                mapping.put("gender", values[2]);
                mapping.put("role_id", Integer.valueOf(values[3]));
                mapping.put("sect_id", Integer.valueOf(values[4]));
                mapping.put("abstract", values[5].substring(1, values[5].length() - 1));
                break;
            case "createAriaPrev":
                assert values.length == 2;
                mapping.put("_aria_id", values[0]);
                mapping.put("aria_id", Integer.valueOf(values[0]));
                mapping.put("title", values[1].substring(1, values[1].length() - 1));
                break;
            case "createUserPrev":
            case "createUser":
                assert values.length == 5;
                mapping.put("_user_id", values[0]);
                mapping.put("user_id", Integer.valueOf(values[0]));
                mapping.put("username", values[1].substring(1, values[1].length() - 1));
                mapping.put("nickname", values[2].substring(2, values[2].length() - 1));
                mapping.put("gender", values[3]);
                mapping.put("register_time", values[4]);
                break;
            case "createTrack":
                assert values.length == 7;
                mapping.put("_track_id", values[0]);
                mapping.put("track_id", Integer.valueOf(values[0]));
                mapping.put("aria_id", Integer.valueOf(values[1]));
                mapping.put("title", values[2].substring(1, values[2].length() - 1));
                mapping.put("players", values[3].substring(1, values[3].length() - 1));
                mapping.put("script", values[4].substring(1, values[4].length() - 1));
                mapping.put("type", values[5]);
                mapping.put("size", Double.valueOf(values[6]));
                break;
            case "createFollow":
                assert values.length == 2;
                mapping.put("edge_id", String.valueOf(recordID));
                mapping.put("from_id", values[0]);
                mapping.put("from_key", "User/" + values[0]);
                mapping.put("to_id", values[1]);
                mapping.put("to_key", "User/" + values[1]);
                break;
            case "createSubscribe":
                assert values.length == 5;
                mapping.put("edge_id", String.valueOf(recordID));
                mapping.put("from_id", values[0]);
                mapping.put("from_key", "User/" + values[0]);
                mapping.put("to_id", values[3]);
                mapping.put("to_key", "Artist/" + values[3]);
                mapping.put("subscribe_time", values[4]);
                break;
            case "createRate":
                assert values.length == 6;
                mapping.put("edge_id", String.valueOf(recordID));
                mapping.put("from_id", values[0]);
                mapping.put("from_key", "User/" + values[0]);
                mapping.put("to_id", values[3]);
                mapping.put("to_key", "Aria/" + values[3]);
                mapping.put("rate_time", values[4]);
                mapping.put("score", Integer.valueOf(values[5]));
                break;
            default:
                break;
        }
    }
}
