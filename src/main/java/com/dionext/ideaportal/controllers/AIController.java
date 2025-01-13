package com.dionext.ideaportal.controllers;


import com.dionext.ai.services.IdeaAIService;
import com.dionext.ideaportal.db.entity.Cite;
import com.dionext.ideaportal.db.repositories.CiteRepository;
import com.dionext.site.controllers.BaseSiteController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/admin"})
class AIController extends BaseSiteController {
	private final IdeaAIService ideaAiService;

	@Autowired
	private CiteRepository citeRepository;

	public AIController(IdeaAIService ideaAiService) {
		this.ideaAiService = ideaAiService;
	}

	@GetMapping("/cite-exp")
	ResponseEntity<String> citeExp(@RequestParam(value = "citeId", defaultValue = "164") int citeId) {
		Cite cite = citeRepository.findById(citeId).orElse(null);
		String exp = ideaAiService.citeExplanation(cite);
		return sendOk("<pre>" + exp + "</pre>");
	}

	@GetMapping("/cite-exp-bulk")
	ResponseEntity<String> citeExpBulk() {
		ideaAiService.citeExplanationBulkAsync();
		return sendOk("Background task is running...");
	}
	@GetMapping("/test-bulk")
	ResponseEntity<String> testBulk() {
		ideaAiService.testAsync();
		return sendOk("Background task is running...");
	}
}
