package io.github.luversof.boot.devcheck.controller.servlet;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckApiController;
import io.github.luversof.boot.devcheck.domain.DevCheckInfo;
import io.github.luversof.boot.devcheck.domain.DevCheckUtilInfo;
import io.github.luversof.boot.devcheck.service.DevCheckUtilInfoService;
import io.github.luversof.boot.devcheck.service.servlet.DevCheckInfoMvcService;

@DevCheckApiController
public class DevCheckApiMvcController {

  private final DevCheckInfoMvcService devCheckInfoMvcService;

  private final DevCheckUtilInfoService devCheckUtilInfoService;

  public DevCheckApiMvcController(
      DevCheckInfoMvcService devCheckInfoMvcService,
      DevCheckUtilInfoService devCheckUtilInfoService) {
    this.devCheckInfoMvcService = devCheckInfoMvcService;
    this.devCheckUtilInfoService = devCheckUtilInfoService;
  }

  @GetMapping("/devCheckInfoList")
  public List<DevCheckInfo> devCheckInfoList() {
    return devCheckInfoMvcService.getDevCheckInfoList();
  }

  @GetMapping("/devCheckUtilInfoList")
  public List<DevCheckUtilInfo> devCheckUtilInfoList() {
    return devCheckUtilInfoService.getDevCheckUtilInfo();
  }
}
