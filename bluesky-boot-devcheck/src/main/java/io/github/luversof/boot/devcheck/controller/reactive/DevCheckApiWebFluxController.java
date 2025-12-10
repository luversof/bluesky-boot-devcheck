package io.github.luversof.boot.devcheck.controller.reactive;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;

import io.github.luversof.boot.devcheck.annotation.DevCheckApiController;
import io.github.luversof.boot.devcheck.domain.DevCheckInfo;
import io.github.luversof.boot.devcheck.domain.DevCheckUtilInfo;
import io.github.luversof.boot.devcheck.service.DevCheckUtilInfoService;
import io.github.luversof.boot.devcheck.service.reactive.DevCheckInfoWebFluxService;

@DevCheckApiController
public class DevCheckApiWebFluxController {
	
	private final DevCheckInfoWebFluxService devCheckInfoWebFluxService;

	private final DevCheckUtilInfoService devCheckUtilInfoService;

	public DevCheckApiWebFluxController(DevCheckInfoWebFluxService devCheckInfoWebFluxService, DevCheckUtilInfoService devCheckUtilInfoService) {
		super();
		this.devCheckInfoWebFluxService = devCheckInfoWebFluxService;
		this.devCheckUtilInfoService = devCheckUtilInfoService;
	}

	@GetMapping("/devCheckInfoList")
	public List<DevCheckInfo> devCheckInfoList(ServerWebExchange exchange) {
		return devCheckInfoWebFluxService.getDevCheckInfoList(exchange);
	}

	@GetMapping("/devCheckUtilInfoList")
	public List<DevCheckUtilInfo> devCheckUtilInfoList() {
		return devCheckUtilInfoService.getDevCheckUtilInfo();
	}

}
