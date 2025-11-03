package webapp.AwesomeCollect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webapp.AwesomeCollect.common.constant.MappingValues;

/**
 * ALBのヘルスチェック用のコントローラークラス。
 */
@RestController
public class HealthCheckController {

  @GetMapping(MappingValues.HEALTH)
  public ResponseEntity<String> healthCheck(){
    return ResponseEntity.ok("OK");
  }
}
