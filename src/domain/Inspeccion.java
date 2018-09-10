package domain;

import java.util.ArrayList;
import java.util.Map;

public class Inspeccion {
    public static String getRankString(ArrayList<Rank> ranks){
        String res= "";
        for(int i=0;i<ranks.size();i++){
            Rank r = ranks.get(i);
            res+= "***********************************Rank: "+(i+1)+
                    "***********************************Rank: "+(i+1)+
                    "***********************************Rank: "+(i+1)+"***********************************" +
                    "\n+Value: "+r.getValue()+"\n"+"+Path: "+r.getPathDoc()+"\n";
        }
        return res;
    }
    public static String getPathString(String path, Map<String, ArrayList<VectorialStruct>> diccionarioGeneral,ArrayList<Map<String,ArrayList<Map<String,Double>>>> historico){
        String res="";
        ArrayList<Map<String,Double>> pesos = historico.get(0).get(path);
        ArrayList<Map<String,Double>> normalizado = historico.get(1).get(path);
        res+="Path: "+path;
        for(int i=0;i<pesos.size();i++){
            Map<String,Double> peso = pesos.get(i);
            Map<String,Double> norma = normalizado.get(i);

            for(String word : peso.keySet()){
                for(VectorialStruct vs : diccionarioGeneral.get(word)){
                    if(vs.getPath().equals(path)){
                        res+= "\n+Termino: "+word+"\n -Frequencia: "+vs.getCantidad()+"\n -Peso: "+peso.get(word)+"\n -Normalizada: "+norma.get(word);
                    }
                }

            }

        }
        return res;
    }

    public static String getWordString(Map<String,Double[]> historicoTerm,String word){
        String res = "Termino: "+word;
        Double[] tmp = historicoTerm.get(word);

        res+= "\n+Ni: "+tmp[0]+"\n+IDF: "+tmp[1];

        return res;
    }
}
