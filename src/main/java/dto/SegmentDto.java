package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Node;

import static java.text.MessageFormat.format;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SegmentDto {

    private Integer id;

    private String feature;

    private String value;

    private String state;

    public static SegmentDto fromNode(Node node) {
        return SegmentDto.builder()
                .id(Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue().trim()))
                .feature(node.getAttributes().getNamedItem("features").getNodeValue().trim())
                .state(node.getAttributes().getNamedItem("state").getNodeValue().trim())
                .value(node.getFirstChild().getNodeValue().trim()).build();
    }

    public String toTsv() {
        return format("{0}\t{1}\t{2}\t{3}", this.id, this.feature, this.state, this.value);
    }
}
