package me.peihao.autoInvest.feign.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.common.Signature;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class BinanceRequestInterceptor implements RequestInterceptor {
  @Override
  public void apply(RequestTemplate template) {
    Collection credentialHeader = template.headers().get("credential");
    if (credentialHeader == null){
      return;
    }
    String credential = template.headers().get("credential").stream().findFirst().orElseThrow(
        () -> new IllegalArgumentException("Fail to generate credential")
    );
    String target = getQueryLineWithoutQuestionMark(template);
    String signature = Signature.encode(credential, target);
    template.query("signature", signature);
    template.removeHeader("credential");
  }

  private static String getQueryLineWithoutQuestionMark(RequestTemplate template) {
    final String queryLineWithoutQuestionMark = template.queryLine().substring(1);
    log.debug("Request Params: {}", queryLineWithoutQuestionMark);
    return template.queryLine().substring(1);
  }
}
