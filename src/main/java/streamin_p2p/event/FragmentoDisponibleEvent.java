package streamin_p2p.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import streamin_p2p.model.Fragmento;

@Getter
public class FragmentoDisponibleEvent extends ApplicationEvent {
    private final Fragmento fragmento;

    public FragmentoDisponibleEvent (Object source, Fragmento fragmento){
        super(source);
        this.fragmento = fragmento;
    }

}
