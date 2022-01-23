package me.peihao.autoInvest.feign.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.common.Signature;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class BinanceRequestInterceptor implements RequestInterceptor {
  @Override
  public void apply(RequestTemplate template) {
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
    log.info("Request Params: {}", queryLineWithoutQuestionMark);
    return template.queryLine().substring(1);
  }
}
