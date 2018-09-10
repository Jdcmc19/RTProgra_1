package view;

import domain.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Controller {
    @FXML
    TextField txtStopwordsInd, txtIndiceInd, txtColeccionInd,txtConsulta,txtIndiceBusqueda,txtEscalafonDir,txtHTMLDir,txtNumDoc,txtEscalafonInspec,txtHistoricoInspec,txtDiccionarioInspec,txtPalabraInspec,txtDocumentoInspec;
    @FXML
    Button btoIniciarInd, btoStopwordsInd, btoColeccionInd, btoIndiceInd,btoIndiceBusqueda,btoEscalafon,btoHTML,btoIniciarBusqueda,btoRankingInspec,btoDocumentoInspec,btoEscalafonInspec,btoHistoricoInspec,btoDiccionarioInspec,btoSearchWord,btoSearchDoc;
    @FXML
    RadioButton rdTfidf, rdBm25;
    @FXML
    TextArea txtArea;

    FileManager fileManager = new FileManager();
    Map<String, String> char200 = new TreeMap<>();
    public void initialize(){
        ToggleGroup tg = new ToggleGroup();
        rdBm25.setToggleGroup(tg);
        rdTfidf.setToggleGroup(tg);


        btoStopwordsInd.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file!=null) txtStopwordsInd.setText(file.getAbsolutePath());
        });
        btoColeccionInd.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            txtColeccionInd.setText(selectedDirectory.getAbsolutePath());
        });
        btoIndiceInd.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            txtIndiceInd.setText(selectedDirectory.getAbsolutePath());
        });
        btoIndiceBusqueda.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            txtIndiceBusqueda.setText(selectedDirectory.getAbsolutePath());
        });
        btoEscalafon.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            txtEscalafonDir.setText(selectedDirectory.getAbsolutePath());
        });
        btoHTML.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            txtHTMLDir.setText(selectedDirectory.getAbsolutePath());
        });
        btoEscalafonInspec.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file!=null) txtEscalafonInspec.setText(file.getAbsolutePath());
        });
        btoHistoricoInspec.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file!=null) txtHistoricoInspec.setText(file.getAbsolutePath());
        });
        btoDiccionarioInspec.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file!=null) txtDiccionarioInspec.setText(file.getAbsolutePath());
        });
        btoDocumentoInspec.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file!=null) txtDocumentoInspec.setText(file.getAbsolutePath());
        });



        TextField[] txt = {txtIndiceInd, txtColeccionInd, txtStopwordsInd,txtIndiceBusqueda,txtEscalafonDir,txtHTMLDir,txtEscalafonInspec,txtHistoricoInspec,txtDiccionarioInspec,txtDocumentoInspec};
        for(TextField t: txt){
            t.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    if (db.hasFiles()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    } else {
                        event.consume();
                    }
                }
            });
            t.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasFiles()) {
                        success = true;
                        String filePath = null;
                        for (File file:db.getFiles()) {
                            filePath = file.getAbsolutePath();
                            t.setText(filePath);
                        }
                    }
                    event.setDropCompleted(success);
                    event.consume();
                }
            });
        }
        btoIniciarInd.setOnAction(event -> {
            String pathColeccion = txtColeccionInd.getText();
            String pathIndice = txtIndiceInd.getText();
            String pathStopwords = txtStopwordsInd.getText();
            String[] stopwords;
            int cantFiles=0;
            ArrayList<String> files;
            Map<String, ArrayList<VectorialStruct>> dicGeneral = new TreeMap<>();
            FileManager fileManager = new FileManager();
            if(!pathColeccion.isEmpty() && !pathIndice.isEmpty() && !pathStopwords.isEmpty()){
                try {
                    files = fileManager.showFiles(pathColeccion);
                    cantFiles = files.size();

                    String stopw = fileManager.getTextFile(pathStopwords);
                    stopwords = stopw.split(",");

                }catch (FileNotFoundException f) {
                    f.printStackTrace();
                    return;
                }
                ArrayList<String> terminos;
                System.out.println("CANTIDAD FILES: "+files.size());
                for(String f: files){
                    try{
                        String text = fileManager.getTextFile(f);
                        text.replace("@","");


                        ///String path = pathColeccion+f.substring(f.lastIndexOf('\\'),f.length());System.out.println(f+"      \n"+path);
                        terminos = fileManager.createMap(text,stopwords,true);
                        dicGeneral = fileManager.getDiccionarioGeneral(f,terminos,dicGeneral);
                    }catch (FileNotFoundException fe){
                        fe.printStackTrace();
                        return;
                    }
                }




                fileManager.saveDiccionario(dicGeneral,pathIndice+"\\DiccionarioGeneral",cantFiles);

            }
        });
        btoIniciarBusqueda.setOnAction(event -> {
            String consulta =  txtConsulta.getText();
            String pathStopwords = txtStopwordsInd.getText();
            String indice = txtIndiceBusqueda.getText();
            String escalafon = txtEscalafonDir.getText();
            String html = txtHTMLDir.getText();
            String numDoc = txtNumDoc.getText();
            int numeroDocumentosRanking=0;

            String[] stopwords;
            if(!consulta.isEmpty() && !pathStopwords.isEmpty() && !escalafon.isEmpty() && !html.isEmpty() && !numDoc.isEmpty() && numDoc.matches("[0-9]+")){
                numeroDocumentosRanking = Integer.parseInt(numDoc);
                try {
                    String stopw = fileManager.getTextFile(pathStopwords);
                    stopwords = stopw.split(",");
                }catch(FileNotFoundException fw){
                    fw.printStackTrace();
                    return;
                }
                ArrayList<String> terminos =  fileManager.createMap(consulta,stopwords,false);
                Map<String,Integer> dicCons = new TreeMap<>();
                dicCons = fileManager.getDiccionarioConsulta(terminos);
                if(rdTfidf.isSelected()){
                    TFIDF tfidf = new TFIDF(indice,dicCons);
                    tfidf.calcularUltimaTabla();
                    fileManager.saveHistorial(tfidf.getHistorico(),indice+"\\Historico");
                    fileManager.saveRanking(tfidf.getRanking(),escalafon+"\\Ranking");
                    fileManager.saveHistorialTermino(tfidf.getHistoricoTerm(),indice+"\\HistoricoTerminos");

                    for(Rank r:tfidf.getRanking()){
                        try {
                            String text = fileManager.getTextFile(r.getPathDoc());
                            String s;
                            if(text.contains(".SH DESCRIPCIÓN")){
                                int ind = text.indexOf(".SH DESCRIPCIÓN");
                                text = text.replaceAll("\n"," ").replace("\t"," ").replaceAll("[\\s+]"," ");
                                if(text.length()>ind+215){
                                    s = text.substring(ind+15,ind+215);
                                }else{
                                    s = text.substring(ind+15);
                                }
                            }else
                               s = "No se encontró :(";

                            char200.put(r.getPathDoc(),s);
                        }catch (FileNotFoundException fe){
                            fe.printStackTrace();
                        }

                    }
                    fileManager.createHTML(html,tfidf.getRanking(),char200,numeroDocumentosRanking);
                }
            }

        });

        btoRankingInspec.setOnAction(event -> {
            String path = txtEscalafonInspec.getText();

            if(!path.isEmpty()){
                ArrayList<Rank> rank = fileManager.readRanking(path);
                String text = Inspeccion.getRankString(rank);
                txtArea.clear();
                txtArea.setText(text);
            }
        });

        btoSearchWord.setOnAction(event -> {
            String pathDic = txtDiccionarioInspec.getText();
            String pathHis = txtHistoricoInspec.getText();
            String pal = txtPalabraInspec.getText();
            if(!pathDic.isEmpty() && !pathHis.isEmpty() && !pal.isEmpty()){
                String tx = Inspeccion.getWordString(fileManager.readHistorialTermino(pathHis),pal);
                txtArea.clear();
                txtArea.setText(tx);
            }

        });
        btoSearchDoc.setOnAction(event -> {
            String pathDic = txtDiccionarioInspec.getText();
            String pathHis = txtHistoricoInspec.getText();
            String path = txtDocumentoInspec.getText();
            if(!pathDic.isEmpty() && !pathHis.isEmpty() && !path.isEmpty()){
                Map<String, ArrayList<VectorialStruct>> diccionarioGeneral = fileManager.readDiccionarioGeneral(pathDic);
                ArrayList<Map<String,ArrayList<Map<String,Double>>>> historico = fileManager.readHistorial(pathHis);
                String texto = Inspeccion.getPathString(path,diccionarioGeneral,historico);
                txtArea.clear();
                txtArea.setText(texto);
            }
        });

    }

}
