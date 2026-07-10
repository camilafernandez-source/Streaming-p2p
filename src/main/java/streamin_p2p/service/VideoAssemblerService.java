package streamin_p2p.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import streamin_p2p.model.Fragmento;

@Service
public class VideoAssemblerService {
    public void reconstruirVideo(Map<Integer, Fragmento> fragmentosDelNodo, String nombreArchivoSalida) throws IOException{
        //verificar que los fragmentos estén completos
        if (fragmentosDelNodo.size() < 10){
            throw new IllegalStateException("Error: faltan fragmentos para reconstruir el video. Existen " + fragmentosDelNodo.size() + "/10");
        }

        //archivo de salida
        try (FileOutputStream fos = new FileOutputStream(nombreArchivoSalida)){
            //unimos pedazos en orden
            for(int i = 1; i <= 10; i++){
                Fragmento frag = fragmentosDelNodo.get(i);
                if (frag != null){
                    fos.write(frag.getContenido());
                }
            }
        }
        System.out.println("Video reconstruido exitosamente en: " + nombreArchivoSalida);
    }
}
