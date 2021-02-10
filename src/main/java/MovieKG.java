import com.github.jsonldjava.core.RDFDataset;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;


import java.io.*;
import java.util.Scanner;

public class MovieKG {
    private KgGUI.OutputConsole console;
    private String dataPath;
    private OntModel model;
    private OntModelSpec spec;
    private RDFFormat outputFormat;

    public Model getModel() {
        return this.model;
    }

    public MovieKG(String dataPath) {
        this.dataPath = dataPath;
        this.spec = OntModelSpec.OWL_MEM;
        outputFormat = RDFFormat.RDFXML;
        // create an empty ontology model without reasoning
        this.model = ModelFactory.createOntologyModel(this.spec);
    }

    public void setConsole(KgGUI.OutputConsole console) {
        this.console = console;
    }

    // 1a: Automatically load an initial ontology from your local file system
    public void loadMovieTTL() {
        // use the RDFDataMgr to find the input file
        String movieTTL = dataPath + "movie.ttl";
        InputStream in = RDFDataMgr.open(movieTTL);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + movieTTL + " not found");
        }

        // read the Turtle file
        this.model.read(in, null, "TTL");
    }

    // 1b: Options for user to select and activate reasoners provided by the chosen APIs
    public void selectReasoner(OntModelSpec spec) {
        this.spec = spec;
    }

    public void applyReasoner() {

        if (this.spec == OntModelSpec.OWL_MEM) {
            this.console.addLine("no reasoner selected");
            return;
        }

        this.model = ModelFactory.createOntologyModel(this.spec, this.model);
        this.console.addLine("applied selected reasoner to model");
        this.console.addLine(this.model.getReasoner().toString());
    }


    // 1c: Export KG in a user-selected format
    public void selectOutputFormat(RDFFormat format) {
        this.outputFormat = format;
    }

    public void exportToFile() {

        String extension = ".owl";
        if (this.outputFormat == RDFFormat.TURTLE) {
            extension = ".ttl";
        } else if (this.outputFormat == RDFFormat.NTRIPLES) {
            extension = ".nt";
        } else if (this.outputFormat == RDFFormat.RDFJSON) {
            extension = ".rj";
        }

        String outputPath = this.dataPath + "output" + extension;

        try {
            OutputStream out = new FileOutputStream(outputPath);
            RDFDataMgr.write(out, this.model, this.outputFormat);
            this.console.addLine("exported file to:");
            this.console.addLine(outputPath);
        } catch (IOException e) {
            this.console.addLine(e.toString());
        }
    }

    // 2a: Add a new ontology class based on user inputs
    public void addOWLClass(String namespace, String className) {

        // add OWL class
        this.model.createClass(namespace + className);

        this.console.addLine("added OWL class:");
        this.console.addLine(namespace + className);
    }

    // 2b: Add a new property based on user inputs, including domain & range definition
    public void addProperty(String nsProperty, String property, String nsDomain, String domain, String nsRange, String range) {

        OntClass domainClass = this.model.getOntClass(nsDomain + domain);
        OntClass rangeClass = this.model.getOntClass(nsRange + range);

        if (domainClass == null) {
            this.console.addLine("DOMAIN not found");
            return;
        } else if (rangeClass == null) {
            this.console.addLine("RANGE not found");
            return;
        }

        // OntClass domain = this.model.getClass(domain);
        ObjectProperty prop = this.model.createObjectProperty(nsProperty + property);

        prop.addDomain(domainClass);
        prop.addRange(rangeClass);

        this.console.addLine("added Property:");
        this.console.addLine(nsProperty + property);
        this.console.addLine("with domain:");
        this.console.addLine(nsDomain + domain);
        this.console.addLine("and range:");
        this.console.addLine(nsRange + range);
    }

    // 2c: Add ontology instances based on user input OR by importing a CSV file
    public void addInstances(String movieName, String actors, String filmStudio, String genre, String director, String writer, String release) {

        String ns = "http://semantics.id/ns/example/movie#";
        String nsEx = "http://semantics.id/ns/example#";

        if (movieName.length() == 0) {
            this.console.addLine("Movie title is missing");
            return;
        }

        // create movie
        OntClass movieClass = this.model.getOntClass(ns + "Movie");
        String movieIndividualName = movieName.toLowerCase().replace(" ", "_");
        Individual movie = movieClass.createIndividual(nsEx + movieIndividualName);
        // add label
        movie.addLabel(movieName, "EN");

        this.console.addLine("Successfully added movie: " + movieName);

        // actor
        if (actors.length() != 0) {
            ObjectProperty hasActor = this.model.getObjectProperty(ns + "hasActor");
            String[] actorsList = actors.split(",");
            for (String a : actorsList) {
                Resource actorResource = this.model.getResource(nsEx + a.trim());
                if (actorResource != null) {
                    movie.addProperty(hasActor, actorResource);
                    this.console.addLine("added actor to instance:" + a);
                } else {
                    this.console.addLine("Actor " + a + " not found in Model");
                    return;
                }
            }
        }


        // film studio
        if (filmStudio.length() != 0) {
            ObjectProperty hasFilmStudio = this.model.getObjectProperty(ns + "hasFilmStudio");
            Resource filmStudioResource = this.model.getResource(nsEx + filmStudio.trim());
            if (filmStudioResource != null) {
                movie.addProperty(hasFilmStudio, filmStudioResource);
                this.console.addLine("added film studio to instance:" + filmStudio);
            } else {
                this.console.addLine("Film Studio " + filmStudio + " not found in Model");
                return;
            }
        }


        // genre
        if (genre.length() != 0) {
            ObjectProperty hasGenre = this.model.getObjectProperty(ns + "hasGenre");
            String[] genreList = genre.split(",");
            for (String g : genreList) {
                Resource genreResource = this.model.getResource(nsEx + g.trim());
                if (genreResource != null) {
                    movie.addProperty(hasGenre, genreResource);
                    this.console.addLine("added genre to instance:" + g);
                } else {
                    this.console.addLine("Genre " + g + " not found in Model");
                    return;
                }
            }
        }

        // director
        if (director.length() != 0) {
            ObjectProperty hasMovieDirector = this.model.getObjectProperty(ns + "hasMovieDirector");
            Resource directorResource = this.model.getResource(nsEx + director.trim());
            if (directorResource != null) {
                movie.addProperty(hasMovieDirector, directorResource);
                this.console.addLine("added movie director to instance:" + director);
            } else {
                this.console.addLine("Movie director " + director + " not found in Model");
                return;
            }
        }

        // writer
        if (writer.length() != 0) {
            ObjectProperty hasWriter = this.model.getObjectProperty(ns + "hasWriter");
            String[] writerList = writer.split(",");
            for (String w : writerList) {
                Resource writerResource = this.model.getResource(nsEx + w.trim());
                if (writerResource != null) {
                    movie.addProperty(hasWriter, writerResource);
                    this.console.addLine("added writer to instance:" + w);
                } else {
                    this.console.addLine("Writer " + w + " not found in Model");
                    return;
                }
            }
        }

        // release date
        Property hasReleaseDate = this.model.getProperty(ns + "hasReleaseDate");
        movie.addProperty(hasReleaseDate, release);
        this.console.addLine("added release date to instance:" + release);
    }

    // 3a: Show a list of queries2 that you have developed in Assignment 2 to users and allow them to choose a query
    // solved in the gui and QueryLoader

    // 3b: Execute the user-selected query on the ontology and show the query results
    public void executeQuery(QueryEnum queryE) {
        QueryLoader queryLoader = new QueryLoader(queryE, this.dataPath);
        QueryType qType = queryLoader.getType();
        Query query = queryLoader.getQuery();

        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.model)) {
            if (qType == QueryType.SELECT) {
                this.console.addLine("Query result " + queryE + ":");
                outputSelectResult(qexec.execSelect());
            } else if (qType == QueryType.ASK) {
                boolean result = qexec.execAsk();
                this.console.addLine("Query result " + queryE + ":");
                this.console.addLine(""+result);
            } else if (qType == QueryType.CONSTRUCT) {
                Model resultModel = qexec.execConstruct();
                this.console.addLine("Query result " + queryE + ":");
                outputModel(resultModel);
            } else if (qType == QueryType.DESCRIBE) {
                Model resultModel = qexec.execDescribe();
                this.console.addLine("Query result " + queryE + ":");
                outputModel(resultModel);
            }
        }
    }

    private void outputSelectResult(ResultSet results) {
        for ( ; results.hasNext() ; ) {
            QuerySolution solution = results.nextSolution();
            this.console.addLine(solution.toString());
        }
    }

    private void outputModel(Model model) {
        StringWriter out = new StringWriter();
        RDFDataMgr.write(out, model, RDFFormat.TURTLE_BLOCKS);
        this.console.addLine(out.toString());
    }
}
