package me.alvin.learn.domain.plantuml.state.domain;

import lombok.*;
import me.alvin.learn.domain.plantuml.constant.PlantumlConstant;
import me.alvin.learn.domain.plantuml.domain.Plantuml;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: Li Xiang
 * Date: 2022/4/27
 * Time: 11:08 AM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StateUml implements Plantuml {
    @Singular(value = "state")
    private List<State> states;

    @Singular(value = "stateRelation")
    private List<StateRelation> stateRelations;

    @Override
    public String toPlantuml() {
        List<String> tokens = new ArrayList<>();
        tokens.add(PlantumlConstant.START_UML);

        List<String> statesUml = Optional.ofNullable(getStates()).orElse(Collections.emptyList()).stream().map(State::toPlantuml).collect(Collectors.toList());
        tokens.addAll(statesUml);

        List<String> stateRelationsUml = Optional.ofNullable(getStateRelations()).orElse(Collections.emptyList()).stream().map(StateRelation::toPlantuml).collect(Collectors.toList());
        tokens.addAll(stateRelationsUml);
        tokens.add(PlantumlConstant.END_UML + "\n");

        return tokens.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining("\n"));
    }
}
