package domain;

import com.sun.deploy.util.StringUtils;
import javafx.scene.control.cell.MapValueFactory;

import java.io.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;


public class FileManager {

    //ArrayList que va a contener los path de los subdirectorios
    private ArrayList<String> subDirPath = new ArrayList<>();

    public FileManager() {
    }

    //Funcion que muerta los archivos por subdirectorio
    public ArrayList<String> showFiles (String directoryPath) throws FileNotFoundException {
        File dicPath = new File(directoryPath);
        ArrayList<String> filesWValidate = new ArrayList<>();
        //Función override obtiene el nombre de los subdirectorios (Convertida a notación lambda)
        String[] directories = dicPath.list((current, name) -> new File(current, name).isDirectory());
        for(int i = 0; i<directories.length; i++)
        {
            subDirPath.add(directoryPath+"\\"+directories[i]);
        }


        //Obtiene todos los archivos por subdirectorio
        for (int y = 0; y<subDirPath.size(); y++)
        {
            File folder = new File(subDirPath.get(y).toString());
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles)
            {
                if (file.isFile())
                {
                    String validate =   validateFile(file.getName());
                    if(!validate.isEmpty())
                    {
                        filesWValidate.add(subDirPath.get(y)+"\\"+validate);
                    }

                }
            }
        }
        for (int i = 0; i < filesWValidate.size(); i++)
        {
            System.out.println(filesWValidate.get(i).toString());
        }
        return filesWValidate;
    }

    public String validateFile(String filePath) throws FileNotFoundException {
        Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(1|2|3|4|5|6|7|8))$)");
            Matcher mtch = fileExtnPtrn.matcher(filePath);
            if(mtch.matches())
            {
                return filePath;

            }
            else return  "";
    }

    public String getTextFile(String path)throws FileNotFoundException{
        File file = new File(path);
        Scanner sc = new Scanner(file);

        sc.useDelimiter("\\Z");
        return sc.next();
    }
    public ArrayList<String> createMap(String text, String[] stop,boolean type)
    {

        String[] lineas = text.split("\n");
        String texto = "";
        for(String linea: lineas){
            linea = linea.trim();
            String[] words = linea.split(" ");
            if(words.length>0 && !words[0].equals(".\\\"")) {
                if(words[0].startsWith(".") && words[0].length()<4){
                    for(int i=1;i<words.length;i++){
                        texto+=(words[i]+" ");
                    }
                }
                else
                    texto+=linea+" ";
            }
        }
        texto = texto.substring(0,texto.length()-1).toLowerCase();
        String[] palabras;
        texto = texto.replace("\t"," ").replace("á","a")
                .replace("é","e").replace("í","i")
                .replace("ó","o").replace("ú","u");
               // .replaceAll("[(){}_,!×`¿=\\[\\]~¡#¬$%º^&«*/\'\"<>?:;|]"," ");
        palabras = texto.split(" ");
        ArrayList<String> terminos = new ArrayList<>();
        for(int i=0;i<palabras.length;i++){
            String[] tm = quitarNonWords(palabras[i],type);
            for(String p : tm){
                terminos.add(p);
            }
        }

        ArrayList<String> stopWords = new ArrayList<>(Arrays.asList(stop));
        for(String palabra : stopWords){
            while(terminos.remove(palabra));
        }

        return  terminos;



    }
    public  Map<String,Integer>  getDiccionarioConsulta(ArrayList<String> terminos){
        Map<String,Integer> dicCon = new TreeMap<>();
        for(int e=0;e<terminos.size();e++){
            String tmp = terminos.get(e);
            if(!tmp.equals("")){
                if(dicCon.containsKey(tmp)){
                    dicCon.put(tmp,dicCon.get(tmp)+1);
                }else{
                    dicCon.put(tmp,1);
                }
            }
        }

        return dicCon;
    }
    public  Map<String, ArrayList<VectorialStruct>>  getDiccionarioGeneral(String path,ArrayList<String> terminos, Map<String, ArrayList<VectorialStruct>> dicGen){
        ArrayList<VectorialStruct> tt;
        for(int e=0;e<terminos.size();e++){
            String tmp = terminos.get(e);
            if(!tmp.equals("")){
                if(dicGen.containsKey(tmp)){
                    tt = dicGen.get(tmp);
                    int pb = -1;
                    for(int i=0;i<tt.size();i++){
                        if(tt.get(i).getPath().equals(path))
                            pb=i;
                    }
                    if(pb<0)tt.add(new VectorialStruct(path,1));
                    else{
                        tt.set(pb,new VectorialStruct(path,tt.get(pb).getCantidad()+1));
                    }
                    dicGen.put(tmp,tt);
                }else{
                    tt = new ArrayList<>();
                    tt.add(new VectorialStruct(path,1));
                    dicGen.put(tmp,tt);
                }
            }
        }

        return dicGen;
    }

    public String[] quitarNonWords(String palabra, boolean type){
        palabra = palabra.replace("_","");
        String[] tmp = palabra.split("\\.");
        Boolean si = true;
        for(String t: tmp){
            if(!t.matches("[0-9]+"))
                si=false;
        }
        if(palabra.contains(".") && si && tmp.length>=2) {
            return palabra.split(" ");
        }
        else if(palabra.startsWith("\\f")){
            String[] h = palabra.split("");
            if(h.length>2 && h[2].matches("[\\w]")){
                palabra=palabra.substring(3,palabra.length());

            }
            if(palabra.contains("ñ"))palabra=palabra.replace("ñ","_");
            palabra = palabra.replaceAll("[\\W]"," ");
            palabra = palabra.replace("_","ñ");
            return palabra.split(" ");

        }
        else if(palabra.startsWith("\\-\\-") && type){
            if(palabra.contains("ñ"))palabra=palabra.replace("ñ","_");
            palabra = palabra.replaceAll("[\\W]"," ");
            palabra = palabra.replace("_","ñ");
            palabra = palabra.trim();
            palabra = "@"+palabra;
            return palabra.split(" ");
        }
        else if(palabra.startsWith("@") && !type){
            palabra = palabra.replace("@","__");
            if(palabra.contains("ñ"))palabra=palabra.replace("ñ","_");
            palabra = palabra.replaceAll("[\\W]"," ");
            palabra = palabra.replace("__","@").replace("_","ñ");
            palabra = palabra.trim();
            return palabra.split(" ");
        }
        else{
            if(palabra.contains("ñ"))palabra=palabra.replace("ñ","_");
            palabra = palabra.replaceAll("[\\W]"," ");
            palabra = palabra.replace("_","ñ");
            return palabra.split(" ");
        }

    }


    public void saveRanking(ArrayList<Rank> rank,String path)
    {
        try{
            File fileOne=new File(path);
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(rank);
            oos.flush();
            oos.close();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void saveDiccionario(Map<String, ArrayList<VectorialStruct>> map,String path, int cantFiles)
    {
        try{
            File fileOne=new File(path);
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            ArrayList<VectorialStruct> vs = new ArrayList<VectorialStruct>();
            vs.add(new VectorialStruct("!",cantFiles));

            map.put("!",vs);
            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void saveHistorial(ArrayList<Map<String,ArrayList<Map<String,Double>>>> map,String path)
    {
        try{
            File fileOne=new File(path);
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<Map<String,ArrayList<Map<String,Double>>>> readHistorial(String path){
        //read from file
        try{
            File toRead=new File(path);
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            ArrayList<Map<String,ArrayList<Map<String,Double>>>> mapInFile=(ArrayList<Map<String,ArrayList<Map<String,Double>>>>)ois.readObject();

            ois.close();
            fis.close();
            //print All data in MAP

            return mapInFile;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void saveHistorialTermino(Map<String,Double[]> map,String path)
    {
        try{
            File fileOne=new File(path);
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Map<String,Double[]> readHistorialTermino(String path){
        //read from file
        try{
            File toRead=new File(path);
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            Map<String,Double[]> mapInFile=(Map<String,Double[]>)ois.readObject();

            ois.close();
            fis.close();
            //print All data in MAP

            return mapInFile;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void printDiccionarioGeneral(Map<String,ArrayList<VectorialStruct>> p ){
        ArrayList<String> aaa = new ArrayList<>(p.keySet());
        int cont = 0;
        for(String m : aaa){
            cont++;
            for(VectorialStruct vs : p.get(m)) {
                if(cont<2000)System.out.println(m + " : " + vs.getPath() + " : " + vs.getCantidad());
            }
        }

    }
    public void printDiccionarioConsulta(Map<String,Integer> p ){
        ArrayList<String> aaa = new ArrayList<>(p.keySet());
        for(String m : aaa){
            System.out.println(m+" : "+p.get(m));
        }

    }
    public TreeMap<String,ArrayList<VectorialStruct>> readDiccionarioGeneral(String path){
        //read from file
        try{
            File toRead=new File(path);
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            TreeMap<String,ArrayList<VectorialStruct>> mapInFile=(TreeMap<String,ArrayList<VectorialStruct>>)ois.readObject();

            ois.close();
            fis.close();
            //print All data in MAP

            return mapInFile;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
        public ArrayList<Rank> readRanking(String path){
        //read from file
        try{
            File toRead=new File(path);
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            ArrayList<Rank> ranks=(ArrayList<Rank>) ois.readObject();

            ois.close();
            fis.close();
            //print All data in MAP
            return ranks;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void createHTML(String path,ArrayList<Rank> ranking,Map<String,String> ch200,int cantidad){
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(path+"\\Ranking.html");
            writer = new BufferedWriter(fWriter);
            writer.write("<header>");
            writer.newLine();
            writer.write("<style> h1{ color:black light} h2 {color: green} </style>");
            writer.write("<h1> Ranking de archivos. </h1>");
            int fin = ranking.size();
            if(ranking.size()>cantidad)fin=cantidad;
            for(int i=0;i<fin;i++){
                Rank r = ranking.get(i);
                writer.write("<h2> Rank: "+(i+1)+"</h2>");
                writer.newLine();
                writer.write("<h3> Path: "+r.getPathDoc()+"</h3>");
                writer.newLine();
                writer.write("<h3> Text: "+ch200.get(r.getPathDoc())+"</h3>");
                System.out.println(ch200.get(r.getPathDoc()).length());
                writer.newLine();
            }
            writer.write("</header>");
             //this is not actually needed for html files - can make your code more readable though
            writer.close(); //make sure you close the writer object
        } catch (Exception e) {
            //catch any exceptions here
        }
    }


}
