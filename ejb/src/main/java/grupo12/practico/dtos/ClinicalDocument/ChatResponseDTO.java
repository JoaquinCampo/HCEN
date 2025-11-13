package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;
import java.util.List;

public class ChatResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String answer;
    private List<ChunkSourceDTO> sources;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<ChunkSourceDTO> getSources() {
        return sources;
    }

    public void setSources(List<ChunkSourceDTO> sources) {
        this.sources = sources;
    }
}
