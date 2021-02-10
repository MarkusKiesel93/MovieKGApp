import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.riot.RDFFormat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KgGUI extends JFrame {
    private MovieKG app;
    private OutputConsole console;
    private JPanel mainPanel;
    private JButton applyReasonerButton;
    private JButton exportButton;
    private JButton addOntologyClassButton;
    private JButton addPropertyButton;
    private JButton addMovieInstanceButton;
    private JButton executeQueryButton;
    private JRadioButton transitiveRadioButton;
    private JRadioButton MICRORuleBasedRadioButton;
    private JRadioButton OWLRuleBasedRadioButton;
    private JRadioButton MINIRuleBasedRadioButton;
    private JRadioButton turtleRadioButton;
    private JRadioButton nTriplesRadioButton;
    private JRadioButton RDFXMLRadioButton;
    private JRadioButton RDFJSONRadioButton;
    private JTextField nsCCTextField;
    private JTextField classNameCCTextField;
    private JTextField nsPropertyPCTextField;
    private JTextField nsDomainPCTextField;
    private JTextField nsRangePCTextField;
    private JTextField propertyPCTextField;
    private JTextField domainPCTextField;
    private JTextField rangePCTextField;
    private JTextArea outputArea;
    private JComboBox selectQueryCBox;
    private JTextField inputMovieTitle;
    private JTextField inputMovieActors;
    private JTextField inputMovieStudio;
    private JTextField inputMovieGenre;
    private JTextField inputMovieDirector;
    private JTextField inputMovieWriter;
    private JTextField inputMovieRelease;


    public KgGUI(MovieKG app) {
        this.console = new OutputConsole(outputArea);
        app.setConsole(this.console);

        setTitle("Movie KG App");
        setSize(800, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(mainPanel);

        // REASONER
        transitiveRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectReasoner(OntModelSpec.OWL_MEM_TRANS_INF);
            }
        });
        OWLRuleBasedRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectReasoner(OntModelSpec.OWL_MEM_RULE_INF);
            }
        });
        MICRORuleBasedRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectReasoner(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
            }
        });
        MINIRuleBasedRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectReasoner(OntModelSpec.OWL_MEM_MINI_RULE_INF);
            }
        });

        applyReasonerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.applyReasoner();
            }
        });

        // EXPORT
        turtleRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectOutputFormat(RDFFormat.TURTLE);
            }
        });
        nTriplesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectOutputFormat(RDFFormat.NTRIPLES);
            }
        });
        RDFXMLRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectOutputFormat(RDFFormat.RDFXML);
            }
        });
        RDFJSONRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.selectOutputFormat(RDFFormat.RDFJSON);
            }
        });
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                app.exportToFile();
            }
        });

        // ADD CLASS
        addOntologyClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String namespace = nsCCTextField.getText();
                String className = classNameCCTextField.getText();
                if (namespace.length() == 0) {
                    console.addLine("please insert namespace");
                } else if (className.length() == 0) {
                    console.addLine("please insert class name");
                } else {
                    app.addOWLClass(namespace, className);
                }
            }
        });

        // ADD PROPERTY
        addPropertyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String namespaceProperty = nsPropertyPCTextField.getText();
                String namespaceDomain = nsDomainPCTextField.getText();
                String namespaceRange = nsRangePCTextField.getText();

                String property = propertyPCTextField.getText();
                String domain = domainPCTextField.getText();
                String range = rangePCTextField.getText();

                if (namespaceProperty.length() == 0) {
                    console.addLine("please insert namespace for property");
                } else if (namespaceDomain.length() == 0) {
                    console.addLine("please insert namespace for domain");
                } else if (namespaceRange.length() == 0) {
                    console.addLine("please insert namespace for range");
                } else if (property.length() == 0) {
                    console.addLine("please insert property name");
                } else if (domain.length() == 0) {
                    console.addLine("please insert domain name");
                } else if (range.length() == 0) {
                    console.addLine("please insert range name");
                } else {
                    app.addProperty(
                            namespaceProperty, property,
                            namespaceDomain, domain,
                            namespaceRange, range);
                }
            }
        });

        // QUERY
        executeQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String choice = String.valueOf(selectQueryCBox.getSelectedItem());
                String queryChoice = choice.split(":")[0];
                QueryEnum qChoice = QueryEnum.valueOf(queryChoice);

                app.executeQuery(qChoice);
            }
        });

        // IMPORT INSTANCES
        addMovieInstanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String movieName = inputMovieTitle.getText();
                String actors = inputMovieActors.getText();
                String filmStudio = inputMovieStudio.getText();
                String genre = inputMovieGenre.getText();
                String director = inputMovieDirector.getText();
                String writer = inputMovieWriter.getText();
                String release = inputMovieRelease.getText();

                app.addInstances(movieName, actors, filmStudio, genre, director, writer, release);
            }
        });
    }

    public class OutputConsole {
        private JTextArea outputArea;
        private String text;

        public OutputConsole(JTextArea outputArea) {
            this.outputArea = outputArea;
            this.text = "";
        }

        public void addLine(String line) {
            this.text = this.text + line + "\n";
            this.outputArea.setText(this.text);
        }
    }
}
