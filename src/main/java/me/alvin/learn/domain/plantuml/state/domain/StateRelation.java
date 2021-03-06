package me.alvin.learn.domain.plantuml.state.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.alvin.learn.domain.plantuml.domain.Plantuml;
import me.alvin.learn.domain.plantuml.state.constant.StatePlantumlConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: Li Xiang
 * Date: 2022/4/27
 * Time: 11:04 AM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StateRelation implements Plantuml {
    private State from;
    private State to;

    @Override
    public String toPlantuml() {
        if (Objects.isNull(from) && Objects.isNull(to)) {
            return StringUtils.EMPTY;
        }
        List<String> tokens = new ArrayList<>(3);
        if(Objects.nonNull(from)){
            tokens.add(getFrom().getName());
            tokens.add(StatePlantumlConstant.TOKEN_RELATION);
        }
        if(Objects.nonNull(to)){
            tokens.add(getTo().getName());
        }
        return String.join(" ", tokens);
    }
}
