package streamin_p2p.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import streamin_p2p.model.Fragmento;

@Service
public class VideoFragmentService {

    public List<Fragmento> dividirVideo() throws IOException {
        //lee el video desde la carpeta
        ClassPathResource res = new ClassPathResource("videos/video.mp4");
        byte[] todoElVideo = res.getInputStream().readAllBytes();

        List<Fragmento> fragmentos = new ArrayList<>();
        int tamanoTotal = todoElVideo.length;
        int tamanoFragmento = tamanoTotal /10;

        for(int i = 0; i < 10; i++){
            int inicio = i * tamanoFragmento;
            int fin = (i==9) ? tamanoTotal : (i + 1) * tamanoFragmento;

            byte[] parte = new byte[fin - inicio];
            System.arraycopy(todoElVideo, inicio, parte, 0, parte.length);

            fragmentos.add(new Fragmento(i+1, parte, "NodoCentral"));
        }
        return fragmentos;
    }
}
