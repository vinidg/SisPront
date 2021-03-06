package br.gov.sp.saobernardo.sispront.solicitacao;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.gov.sp.saobernardo.sispront.papel.Nomes;
import br.gov.sp.saobernardo.sispront.resposta.Resposta;
import br.gov.sp.saobernardo.sispront.resposta.Status;
import br.gov.sp.saobernardo.sispront.usuario.Usuario;
import br.gov.sp.saobernardo.sispront.usuario.UsuarioAdaptador;
import br.gov.sp.saobernardo.sispront.usuario.Usuarios;

@Controller
public class HomeController {
	
	@Autowired
	UsuarioAdaptador usuarioAdaptador;
	
	@Autowired
	private Solicitacoes solicitacoes;
	
	@Autowired
	private Usuarios usuarios;
	
	@Autowired
	private MessageSource msgSource;
	
	/** 
	 * @Deprecated Somente aos olhos do Spring
	 */
	
	@Deprecated
	HomeController(){
		
	}
	
	@RequestMapping(value = "/a/solicitacao/home" , method = RequestMethod.GET)
	public String mostraHome(Model model) {
		Usuario usuario = usuarioAdaptador.obterUsuarioLogado();
		model.addAttribute("nomeDoUsuario",usuario);
		
		return "solicitacao/home";
	}
	
	@Secured(Nomes.ROLE_SOLICITANTE)
	@RequestMapping(value = "/a/solicitacao/nova" , method = RequestMethod.GET)
	public String mostraFormularioDeCadastro() {
		return "solicitacao/formularioNovaSolicitacao";
	}
	
	@Secured(Nomes.ROLE_SOLICITANTE)
	@Transactional
	@RequestMapping(value = "/a/solicitacao/nova" , method = RequestMethod.POST)
	public String salvarCadastro(@Valid SolicitacaoFormulario solicitacaoFormulario, Model model,
			BindingResult result, RedirectAttributes redirect) {

		Solicitacao solicitacao = solicitacaoFormulario.convertePara(new Solicitacao());
		Usuario usuario = usuarioAdaptador.obterUsuarioLogado();
		solicitacao.setAutor(usuario);
		solicitacao.setUnidade(usuario.getUnidade());
		
		Resposta resposta2 = new Resposta(Status.Aguardando_Levantamento_de_Ficha, "", "",
				usuario);
		
		if(result.hasErrors()){
			return "solicitacao/formularioNovaSolicitacao";
		}
		
		solicitacoes.adicionaSolicitacao(solicitacao);
		solicitacao.insereResposta(solicitacoes, resposta2);
		
		String msg = msgSource.getMessage("solicitacao.cadastro.sucesso", null, "", new Locale("pt", "BR"));
		redirect.addFlashAttribute("msgSucesso", msg);
		
		return "redirect:/a/solicitacao/todos";
	}

	@Secured({ Nomes.ROLE_SOLICITANTE, Nomes.ROLE_MONITOR })
	@RequestMapping(value = "/a/solicitacao/todos", method = RequestMethod.GET)
	public String mostraListaTodos(Model model){
		Usuario usuario = usuarioAdaptador.obterUsuarioLogado();
		usuario = usuarios.buscaPorId(usuario.getId());
		
		List<Solicitacao> novasSolicitacoes = solicitacoes.buscaSolicitacoes(usuario.getUnidade());
		model.addAttribute("novasSolicitacoes", novasSolicitacoes);
		
		return "solicitacao/listaAbertas";
	}
	
	@Secured({ Nomes.ROLE_SOLICITANTE, Nomes.ROLE_MONITOR })
	@RequestMapping(value = "/a/solicitacao/{id}", method = RequestMethod.GET)
	public String mostraSolicitacao(@PathVariable("id") Long id, Model model, HttpSession session) {
		Solicitacao solicitacao = solicitacoes.buscaPorId(id);
		model.addAttribute("solicitacao", solicitacao);
		
		Collection<Resposta> respostas = solicitacao.getRespostas();
		model.addAttribute("respostas", respostas);

		return "solicitacao/mostraSolicitacao";
	}
	
	@Secured(Nomes.ROLE_SOLICITANTE)
	@RequestMapping(value = "/a/solicitacao/{id}/status", method = RequestMethod.GET)
	public String mostraAlterarStatus(@PathVariable("id") Long id, Model model){
		Solicitacao solicitacao = solicitacoes.buscaPorId(id);
		
		if (solicitacao == null) {
			throw new SolicitacaoNaoEncontradoException(id);
		}
		
		model.addAttribute("status", Status.values());
		model.addAttribute("solicitacao", solicitacao);
		return "solicitacao/alterarResposta";
	}

	@Secured(Nomes.ROLE_SOLICITANTE)
	@Transactional
	@RequestMapping(value = "/a/solicitacao/{id}/status", method = RequestMethod.POST)
	public String salvarAlterarStatus(@PathVariable("id") Long id, Resposta resposta, Model model,
			BindingResult result, RedirectAttributes redirect){
		
		Solicitacao solicitacao = solicitacoes.buscaPorId(id);
		
		if (solicitacao == null) {
			throw new SolicitacaoNaoEncontradoException(id);
		}
		Usuario usuario = usuarioAdaptador.obterUsuarioLogado();
		Resposta resposta2 = new Resposta(resposta.getResposta(), resposta.getMotivo(), resposta.getRg(),
				usuario);
		
		if(result.hasErrors()){
			return "solicitacao/alterarResposta";
		}
		solicitacao.insereResposta(solicitacoes, resposta2);
		
		
		String msg = msgSource.getMessage("solicitacao.cadastro.sucesso", null, "", new Locale("pt", "BR"));
		redirect.addFlashAttribute("msgSucesso", msg);
		
		return "redirect:/a/solicitacao/todos";
	}
	
}
