import org.apache.jena.query.QueryFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class QueryLoader {
    private String queryFilePath;
    private String queryString;
    private QueryType qType;

    public QueryLoader(QueryEnum query, String dataPath) {
        queryFilePath = dataPath + "querys/";
        this.queryString = "";
        readWholeFile(query.toString());
        setQueryType(query);
    }

    public org.apache.jena.query.Query getQuery(){
        return QueryFactory.create(this.queryString);
    }

    public QueryType getType() {
        return this.qType;
    }

    private void setQueryType(QueryEnum query) {
        switch (query) {
            case Q4:
                this.qType = QueryType.ASK;
                break;
            case Q8:
                this.qType = QueryType.CONSTRUCT;
                break;
            case Q6:
                this.qType = QueryType.DESCRIBE;
                break;
            default:
                this.qType = QueryType.SELECT;
                break;
        }
    }

    private void readWholeFile(String name) {

        String path = queryFilePath + name;

        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                this.queryString += line;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("QUERY FILE NOT FOUND");
            e.printStackTrace();
        }
    }
}
