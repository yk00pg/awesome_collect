package webapp.AwesomeCollect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ALBのヘルスチェック用のコントローラークラス。
 */
@RestController
public class HealthCheckController {

  @GetMapping("/health")
  public ResponseEntity<String> healthCheck(){
    return ResponseEntity.ok("OK");
  }
}
