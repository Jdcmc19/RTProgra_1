package domain;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.*;
import java.lang.Math;

public class TFIDF {
    private int N;
    private Map<String, ArrayList<VectorialStruct>> diccionarioGeneral;
    private Map<String, ArrayList<VectorialStruct>> diccionarioArchivos = new TreeMap<>();
    private Map<String, Integer>  diccionarioConsulta;
    private Map<String,ArrayList<Map<String,Double>>> wijCalculada = new TreeMap<>();
    private Map<String, Double> wijConsulta = new TreeMap<>();
    private ArrayList<Rank> ranking = new ArrayList<>();

    private ArrayList<Map<String,ArrayList<Map<String,Double>>>> historico = new ArrayList<>();
    //private Map<String,ArrayList<Map<String,Double[]>>> historicoTerm = new TreeMap<>();
    private Map<String,Double[]> historicoTerm = new TreeMap<>();

    public TFIDF(String path, Map<String,Integer> dicConsulta) {
        FileManager fm = new FileManager();
        this.diccionarioGeneral = fm.readDiccionarioGeneral(path+"\\DiccionarioGeneral");
        this.diccionarioConsulta = dicConsulta;
        VectorialStruct t = this.diccionarioGeneral.get("!").get(0);
        if (t.getPath().equals("!")){
            this.N = t.getCantidad();
            this.diccionarioGeneral.remove("!");
        }
        getDiccionarioArchivos();

    }

    public TFIDF(Map<String, ArrayList<VectorialStruct>> diccionarioGeneral, Map<String, Integer> diccionarioConsulta, int N) {
        this.diccionarioGeneral = diccionarioGeneral;
        this.diccionarioConsulta = diccionarioConsulta;
        this.N = N;
        this.diccionarioGeneral.remove("!");
        getDiccionarioArchivos();

    }

    public Double getIDF(int ni){
        return Math.log10(N*1.0/ni*1.0)/Math.log10(2.0);
    }
    public Double getTF(int freqij){
        return 1+(Math.log10(freqij)/Math.log10(2.0));
    }
    public Double getWij(String word,String documentoPath){
        ArrayList<VectorialStruct>  vs = this.diccionarioGeneral.get(word);
        int freqij =0;
        Double ni=0.0;
        if(vs != null && vs.size()>0){
            ni = vs.size()*1.0;
            Double idf = getIDF(ni.intValue());
            Double[] t = {ni,idf};

            if(!historicoTerm.containsKey(word)){
                historicoTerm.put(word,t);
            }
            for(VectorialStruct vv : vs){
                if(vv.getPath().equals(documentoPath)){
                    freqij = vv.getCantidad();
                }
            }
            return getTF(freqij)*idf;
        }
        return 0.0;
    }
    public Double getWijConsulta(String word){
        ArrayList<VectorialStruct> pb;
        if (this.diccionarioGeneral.containsKey(word)){
            pb = new ArrayList<VectorialStruct>(this.diccionarioGeneral.get(word));
            int freq = this.diccionarioConsulta.get(word);
            int ni = pb.size();
            if(ni!=0 && freq!=0){
                return getTF(freq)*getIDF(ni);
            }else
                return 0.0;
        }
        else
            return 0.0;

    }
    public Map<String,Double> getNormas(){
        Map<String,Double> map = new TreeMap<>();
        Map<String,ArrayList<Map<String,Double>>> pesos = new TreeMap<>();
        for(String path : this.diccionarioArchivos.keySet()){
            Double norma = 0.0;
            for(VectorialStruct vs: this.diccionarioArchivos.get(path)){
                Double value = getWij(vs.getPath(),path);
                Map<String,Double> tmp = new TreeMap<>();
                tmp.put(vs.getPath(),value);
                if(wijCalculada.containsKey(path)){
                    ArrayList<Map<String,Double>> tm = wijCalculada.get(path);
                    tm.add(tmp);
                    wijCalculada.put(path,tm);
                }else{
                    ArrayList<Map<String,Double>> tm = new ArrayList<>();
                    tm.add(tmp);
                    wijCalculada.put(path,tm);
                }

                norma+=Math.pow(value,2);
            }
            norma = Math.sqrt(norma);
            map.put(path,norma);
        }
        for(String a : wijCalculada.keySet()){
            pesos.put(a,new ArrayList<>(wijCalculada.get(a)));
        }
        historico.add(pesos);
        return map;
    }
    public Double getNormaConsulta(){
        Double norma = 0.0;
        for(String word: this.diccionarioConsulta.keySet()){
            Double value = getWijConsulta(word);
            wijConsulta.put(word,value);
            norma+=Math.pow(value,2);
        }
        norma = Math.sqrt(norma);
        return norma;
    }
    public void normalizarConsulta(){
        Double norma = getNormaConsulta();
        for(String word : this.diccionarioConsulta.keySet()){
            Double val = this.wijConsulta.get(word);
            System.out.println("anterior: "+ val+" norma: "+norma);
            this.wijConsulta.put(word,val/norma);
        }


        for(String word: this.diccionarioConsulta.keySet()){
            System.out.println(word+ " : " + this.wijConsulta.get(word));
        }
    }
    public void normalizarWij(){
        Map<String,Double> map = getNormas();
        for(String mm : wijCalculada.keySet()){
            for(int i=0;i<wijCalculada.get(mm).size();i++){
                Map<String,Double> mp = wijCalculada.get(mm).get(i);
                for(String key:mp.keySet()){
                   // System.out.println("HERE: "+mm+" : "+key+" : "+mp.get(key));
                    Map<String,Double> temp1 = new TreeMap<>();
                    temp1.put(key,mp.get(key)/map.get(mm));
                    wijCalculada.get(mm).set(i,temp1);
                }
            }
        }
        Map<String,ArrayList<Map<String,Double>>> normalizada =  new TreeMap(wijCalculada);
        historico.add(normalizada);
    }
    public void calcularUltimaTabla(){
        normalizarWij();
        normalizarConsulta();
        Map<String,ArrayList<Map<String,Double>>> last = new TreeMap();
        for(String mm : wijCalculada.keySet()){
            for(int i=0;i<wijCalculada.get(mm).size();i++){
                Map<String,Double> mp = wijCalculada.get(mm).get(i);

                for(String key:wijConsulta.keySet()){
                    Double factor2 = wijConsulta.get(key);
                    Double factor1 = mp.get(key);
                    Double total;
                    if(factor2!=null && factor1!=null){
                        total=factor1*factor2;
                        Map<String,Double> current = new TreeMap<>();
                        current.put(key,total);
                        ArrayList<Map<String,Double>> tmp;
                        if(last.containsKey(mm)){
                            tmp = last.get(mm);
                        }else{
                            tmp = new ArrayList<>();
                        }

                        tmp.add(current);
                        last.put(mm,tmp);
                    }

                }
            }
        }
        wijCalculada = last;
        Double tot =0.0;
        for(String doc:wijCalculada.keySet()){
            for(Map<String,Double> map:wijCalculada.get(doc)){
                for(String word:map.keySet()){
                    tot+=map.get(word);
                }
            }
            ranking.add(new Rank(doc,tot));
            tot = 0.0;
        }
        Collections.sort(ranking);

        for(int i=0;i<ranking.size();i++){
            Rank d = ranking.get(i);
            System.out.println(i+" :"+d.toString());
        }


    }
    public void getDiccionarioArchivos(){
        for(String word : this.diccionarioGeneral.keySet()){
            for(VectorialStruct vs: this.diccionarioGeneral.get(word)){
                if(this.diccionarioArchivos!=null && this.diccionarioArchivos.containsKey(vs.getPath())){
                    ArrayList<VectorialStruct> tmp = this.diccionarioArchivos.get(vs.getPath());
                    tmp.add(new VectorialStruct(word,vs.getCantidad()));
                    this.diccionarioArchivos.put(vs.getPath(),tmp);
                }else{
                    ArrayList<VectorialStruct> t = new ArrayList<>();
                    t.add(new VectorialStruct(word,vs.getCantidad()));
                    this.diccionarioArchivos.put(vs.getPath(),t);
                }
            }
        }
    }

    public Map<String, ArrayList<Map<String, Double>>> getWijCalculada() {
        return wijCalculada;
    }

    public ArrayList<Rank> getRanking() {
        return ranking;
    }

    public ArrayList<Map<String, ArrayList<Map<String, Double>>>> getHistorico() {
        return historico;
    }

    public Map<String,Double[]> getHistoricoTerm() {
        return historicoTerm;
    }
}
