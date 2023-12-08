package br.com.renato.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {
    public static String obterTraducao(String texto) {
        OpenAiService service = new OpenAiService("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");

        CompletionRequest requisicao = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        var resposta = service.createCompletion(requisicao);
        return resposta.getChoices().get(0).getText();
    }
}
