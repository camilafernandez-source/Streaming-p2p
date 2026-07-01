package streamin_p2p.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Fragmento {
    private int id;            //Identificador del fragmento
    private byte[] contenido;  //Contenido del video
    private String nodoOrigen; //nodo
}
