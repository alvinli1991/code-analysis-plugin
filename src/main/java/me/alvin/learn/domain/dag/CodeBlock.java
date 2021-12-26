package me.alvin.learn.domain.dag;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Li Xiang
 * Date: 2021/12/27
 * Time: 9:45 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CodeBlock {
    /**
     * 代码行
     */
    private int lineNumber;
    /**
     * 表示输入的代码
     */
    private String codeBlock;
}
