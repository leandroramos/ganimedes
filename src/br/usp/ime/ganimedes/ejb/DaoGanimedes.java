package br.usp.ime.ganimedes.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.usp.ime.ganimedes.model.Aluno;
import br.usp.ime.ganimedes.model.Contato;
import br.usp.ime.ganimedes.model.Convenio;
import br.usp.ime.ganimedes.model.Curso;
import br.usp.ime.ganimedes.model.Documento;
import br.usp.ime.ganimedes.model.EStatusDoc;
import br.usp.ime.ganimedes.model.ETipoDoc;
import br.usp.ime.ganimedes.model.Empresa;
import br.usp.ime.ganimedes.model.Estagio;
import br.usp.ime.ganimedes.model.Jornada;
import br.usp.ime.ganimedes.model.Notificacao;
import br.usp.ime.ganimedes.model.Remuneracao;
import br.usp.ime.ganimedes.model.Supervisor;
import br.usp.ime.ganimedes.model.Token;
import br.usp.ime.util.OrdenadorEstagioDtaIni;

@Stateless
public class DaoGanimedes {

	@PersistenceContext(unitName = "ganimedes")
	EntityManager em;

	public List<Aluno> buscarAlunos() {
		List<Aluno> alunos = new ArrayList<Aluno>();
		String q = "SELECT A FROM Aluno A ORDER BY A.nompes ASC";
		Query query = em.createQuery(q);
		alunos = query.getResultList();
		return alunos;
	}

	public List<Documento> buscarDocumentos(Estagio e) {
		List<Documento> documentos = new ArrayList<Documento>();
		String q = "SELECT D FROM Documento D WHERE D.estagio = :estagio ORDER BY D.dtaini ASC";
		Query query = em.createQuery(q);
		query.setParameter("estagio", e);
		documentos = query.getResultList();
		return documentos;
	}

	public List<Remuneracao> buscarRemuneracoes(Estagio e) {
		List<Remuneracao> remuneracoes = new ArrayList<Remuneracao>();
		String q = "SELECT R FROM Remuneracao R WHERE R.estagio = :estagio ORDER BY R.dtaini ASC";
		Query query = em.createQuery(q);
		query.setParameter("estagio", e);
		remuneracoes = query.getResultList();
		return remuneracoes;
	}

	public List<Supervisor> buscarSupervisores(Estagio e) {
		List<Supervisor> supervisores = new ArrayList<Supervisor>();
		String q = "SELECT S FROM Supervisor S WHERE S.estagio = :estagio ORDER BY S.dtaini ASC";
		Query query = em.createQuery(q);
		query.setParameter("estagio", e);
		supervisores = query.getResultList();
		return supervisores;
	}

	public List<Notificacao> buscarNotificacoes(Estagio e) {
		List<Notificacao> notificacoes = new ArrayList<Notificacao>();
		String q = "SELECT N FROM Notificacao N WHERE N.estagio = :estagio ORDER BY N.data ASC";
		Query query = em.createQuery(q);
		query.setParameter("estagio", e);
		notificacoes = query.getResultList();
		return notificacoes;
	}

	public List<Jornada> buscarJornadas(Estagio e) {
		List<Jornada> jornadas = new ArrayList<Jornada>();
		String q = "SELECT J FROM Jornada J WHERE J.estagio = :estagio ORDER BY J.dtaini ASC";
		Query query = em.createQuery(q);
		query.setParameter("estagio", e);
		jornadas = query.getResultList();
		return jornadas;
	}

	public List<Curso> buscarCursos() {
		List<Curso> cursos = new ArrayList<Curso>();

		String q = "SELECT C FROM Curso C ORDER BY C.nivel, C.nome";
		Query query = em.createQuery(q);

		cursos = query.getResultList();

		return cursos;

	}

	public List<Empresa> buscarEmpresas() {
		List<Empresa> empresas = new ArrayList<Empresa>();

		String q = "SELECT E FROM Empresa E ORDER BY E.nome ASC";
		Query query = em.createQuery(q);

		empresas = query.getResultList();

		return empresas;

	}

	public List<Empresa> buscarEmpresaPorNome(String criterio) {
		List<Empresa> empresas = new ArrayList<Empresa>();

		String q = "SELECT E FROM Empresa E WHERE E.nome like :criterio";
		Query query = em.createQuery(q);
		query.setParameter("criterio", "%" + criterio + "%");
		empresas = query.getResultList();

		return empresas;
	}

	public Empresa buscarEmpresaPorId(Integer id) {
		List<Empresa> empresas = new ArrayList<Empresa>();
		String q = "SELECT E FROM Empresa E WHERE E.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", id);
		empresas = query.getResultList();

		if (!empresas.isEmpty()) {
			return empresas.get(0);
		}

		return null;
	}

	public List<Empresa> buscarEmpresaPorCnpj(String criterio) {
		List<Empresa> empresas = new ArrayList<Empresa>();

		String q = "SELECT E FROM Empresa E WHERE E.cnpj =:criterio";
		Query query = em.createQuery(q);
		query.setParameter("criterio", criterio);
		empresas = query.getResultList();
		return empresas;

	}

	public List<Empresa> buscarEmpresasConveniadas(Date dta) {
		List<Convenio> convenios = new ArrayList<Convenio>();

		String q = " SELECT C FROM Convenio C WHERE :dta BETWEEN C.dtaini AND C.dtafim";
		Query query = em.createQuery(q);
		query.setParameter("dta", dta);

		convenios = query.getResultList();

		List<Empresa> empresas = new ArrayList<Empresa>();

		for (Convenio convenio : convenios) {
			empresas.add(convenio.getEmpresa());
		}

		for (Empresa empresa : empresas) {
			empresa.setConvenios(this.buscarConvenios(empresa));
		}

		return empresas;

	}

	public List<Empresa> buscarEmpresasConveniadasProximoEncerramento() {
		List<Convenio> convenios = new ArrayList<Convenio>();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.add(Calendar.MONTH, 2);

		Date df = cal.getTime();
		Date hoje = new Date();

		String q = " SELECT C FROM Convenio C WHERE  C.dtafim BETWEEN :hoje AND :d";
		Query query = em.createQuery(q);
		query.setParameter("d", df);
		query.setParameter("hoje", hoje);

		convenios = query.getResultList();

		List<Empresa> empresas = new ArrayList<Empresa>();

		for (Convenio convenio : convenios) {
			empresas.add(convenio.getEmpresa());
		}

		for (Empresa empresa : empresas) {
			empresa.setConvenios(this.buscarConvenios(empresa));
		}

		return empresas;

	}

	public List<Contato> buscarContatos(Empresa empresa) {
		List<Contato> contatos = new ArrayList<Contato>();
		String q = "SELECT C FROM Contato C WHERE C.empresa = :empresa";
		Query query = em.createQuery(q);
		query.setParameter("empresa", empresa);
		contatos = query.getResultList();

		return contatos;

	}

	public List<Convenio> buscarConvenios(Empresa empresa) {
		List<Convenio> convenios = new ArrayList<Convenio>();
		String q = "SELECT C FROM Convenio C WHERE C.empresa = :empresa";
		Query query = em.createQuery(q);
		query.setParameter("empresa", empresa);
		convenios = query.getResultList();

		return convenios;

	}

	public Estagio buscarEstagio(Integer id) {
		String q = "SELECT E FROM Estagio E WHERE E.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", id);
		return (Estagio) query.getSingleResult();
	}

	public List<Estagio> buscarEstagios(Empresa empresa) {
		List<Estagio> estagios = new ArrayList<Estagio>();
		String q = "SELECT E FROM Estagio E WHERE E.empresa = :empresa";
		Query query = em.createQuery(q);
		query.setParameter("empresa", empresa);
		estagios = query.getResultList();

		for (Estagio e : estagios) {
			e.setDocumentos(this.buscarDocumentos(e));
			e.setRemuneracoes(this.buscarRemuneracoes(e));
			e.setJornadas(this.buscarJornadas(e));
			e.setSupervisores(this.buscarSupervisores(e));
			e.setNotificacoes(this.buscarNotificacoes(e));
		}

		return estagios;

	}

	public List<Estagio> buscarEstagios(Aluno aluno) {
		List<Estagio> estagios = new ArrayList<Estagio>();
		String q = "SELECT E FROM Estagio E WHERE E.aluno = :aluno";
		Query query = em.createQuery(q);
		query.setParameter("aluno", aluno);
		estagios = query.getResultList();

		for (Estagio e : estagios) {
			e.setDocumentos(this.buscarDocumentos(e));
			e.setRemuneracoes(this.buscarRemuneracoes(e));
			e.setJornadas(this.buscarJornadas(e));
			e.setSupervisores(this.buscarSupervisores(e));
			e.setNotificacoes(this.buscarNotificacoes(e));
		}

		// ordena os estagios pela data de entrada do inicio crescente
		Collections.sort(estagios, new OrdenadorEstagioDtaIni());

		return estagios;

	}

	public List<Documento> buscarDocumentosAtivosPeriodo(Date dtaini, Date dtafim) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		List<Documento> documentos = new ArrayList<Documento>();

		if (dtaini.equals(dtafim)) {
			String q = "SELECT D FROM Documento D WHERE :data BETWEEN D.dtaini AND D.dtafim";
			Query query = em.createQuery(q);
			query.setParameter("data", dtaini);
			documentos = query.getResultList();

		} else {
			String q = "SELECT D FROM Documento D WHERE D.dtaini >= :dtaini AND D.dtafim <= :dtafim";
			Query query = em.createQuery(q);
			query.setParameter("dtaini", dtaini);
			query.setParameter("dtafim", dtafim);
			documentos = query.getResultList();
		}

		for (Documento documento : documentos) {
			Estagio e = this.buscarEstagio(documento.getEstagio().getId());
			documento.setEstagio(e);

		}

		return documentos;

	}

	public List<Estagio> buscarEstagiosAtivosPeriodo(Date dtaini, Date dtafim) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		List<Estagio> estagios = new ArrayList<Estagio>();

		String q = "SELECT DISTINCT E.id, E.aluno_codpes, E.empresa_id FROM ESTAGIO AS E, DOCUMENTO AS D WHERE E.id = D.estagio_id AND (:dtaini BETWEEN D.dtaini AND D.dtafim OR :dtafim BETWEEN D.dtaini AND D.dtafim) OR (D.dtaini <= :dtaini AND :dtafim >= D.dtafim) AND D.statusDoc = :status_doc AND D.tipo = :tipo_doc AND aluno_codpes != null";
		Query query = em.createNativeQuery(q);
		query.setParameter("dtaini", dtaini);
		query.setParameter("dtafim", dtafim);
		query.setParameter("tipo_doc", ETipoDoc.COM);
		query.setParameter("status_doc", EStatusDoc.DEFERIDO);
		List<Object[]> resultado = query.getResultList();

		if (!resultado.isEmpty()) {
			for (Object[] object : resultado) {
				Aluno a = new Aluno((int) object[1]);
				Estagio e = new Estagio((int) object[0]);
				e.setEmpresa((Empresa) this.buscarEmpresaPorId((int) object[2]));
				e.setDocumentos(this.buscarDocumentos(e));
				e.setAluno(a);
				estagios.add(e);
			}
			return new ArrayList<Estagio>(estagios);
		}
		return null;
	}

	public List<Estagio> buscarEstagiosAtivosPeriodo(Date dtaini, Date dtafim, Empresa empresa) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		List<Documento> documentos = new ArrayList<Documento>();

		Set<Estagio> estagios = new HashSet<Estagio>();

		if (dtaini.equals(dtafim)) {
			String q = "SELECT D FROM Documento D WHERE :data BETWEEN D.dtaini AND D.dtafim";
			Query query = em.createQuery(q);
			query.setParameter("data", dtaini);
			documentos = query.getResultList();

		} else {
			String q = "SELECT D FROM Documento D WHERE D.dtaini >= :dtaini AND D.dtafim <= :dtafim";
			Query query = em.createQuery(q);
			query.setParameter("dtaini", dtaini);
			query.setParameter("dtafim", dtafim);
			documentos = query.getResultList();
		}

		for (Documento documento : documentos) {
			Estagio e = this.buscarEstagio(documento.getEstagio().getId());
			if (e.isAtivo()) {
				estagios.add(e);
			}
		}

		List<Estagio> estagiosList = new ArrayList<Estagio>();
		estagiosList.addAll(estagios);
		return estagiosList;
	}

	public List<Estagio> buscarEstagios() {
		List<Estagio> estagios = new ArrayList<Estagio>();
		String q = "SELECT E FROM Estagio E";
		Query query = em.createQuery(q);

		estagios = query.getResultList();

		for (Estagio estagio : estagios) {

			estagio.setDocumentos(this.buscarDocumentos(estagio));
			estagio.setRemuneracoes(this.buscarRemuneracoes(estagio));
			estagio.setJornadas(this.buscarJornadas(estagio));
			estagio.setSupervisores(this.buscarSupervisores(estagio));
			estagio.setNotificacoes(this.buscarNotificacoes(estagio));
		}

		return estagios;

	}

	public List<Estagio> buscarEstagiosEncerrados() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		List<Estagio> estagios = new ArrayList<Estagio>();
		String q = "SELECT E FROM Estagio E WHERE :hoje > E.dtafim ORDER BY E.dtaini";
		Query query = em.createQuery(q);
		query.setParameter("hoje", c.getTime());
		estagios = query.getResultList();

		for (Estagio estagio : estagios) {
			estagio.setDocumentos(this.buscarDocumentos(estagio));
			estagio.setRemuneracoes(this.buscarRemuneracoes(estagio));
			estagio.setJornadas(this.buscarJornadas(estagio));
			estagio.setSupervisores(this.buscarSupervisores(estagio));
			estagio.setNotificacoes(this.buscarNotificacoes(estagio));

		}
		return estagios;
	}

	public Estagio buscarEstagio(Aluno a, Empresa e) {
		String q = "SELECT E FROM Estagio AS E WHERE E.aluno.id = :codpes AND E.empresa.id = :codemp";
		Query query = em.createQuery(q);
		query.setParameter("codpes", a.getCodpes());
		query.setParameter("codemp", e.getId());

		List<Estagio> estagios = query.getResultList();

		if (!estagios.isEmpty()) {

			Estagio estagio = estagios.get(0);

			estagio.setDocumentos(this.buscarDocumentos(estagio));
			estagio.setRemuneracoes(this.buscarRemuneracoes(estagio));
			estagio.setJornadas(this.buscarJornadas(estagio));
			estagio.setSupervisores(this.buscarSupervisores(estagio));
			estagio.setNotificacoes(this.buscarNotificacoes(estagio));
			return estagio;
		}

		return null;

	}

	public void deletarContato(Contato c) {
		String q = "DELETE FROM Contato AS C WHERE C.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", c.getId());
		query.executeUpdate();
	}

	public void deletarConvenio(Convenio c) {
		String q = "DELETE FROM Convenio AS C WHERE C.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", c.getId());
		query.executeUpdate();
	}

	public void deletarEstagio(Estagio e) {
		this.deletarDocumentos(e);

		String q = "DELETE FROM Estagio AS E WHERE E.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", e.getId());
		query.executeUpdate();
	}

	public void deletarDocumentos(Estagio e) {
		// Query Nativa...

		String q = "DELETE D FROM DOCUMENTO AS D WHERE D.estagio_id = :id";
		Query query = em.createNativeQuery(q);
		query.setParameter("id", e.getId());
		query.executeUpdate();
	}

	public void removerConfirmacaoEmail(Token confirmacaoEmail) {
		this.deletar(confirmacaoEmail);
	}

	public Object salvar(Object o) {

		try {
			o = em.merge(o);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return o;
	}

	public Object atualizar(Object o) {

		try {
			o = em.merge(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public void deletar(Object o) {

		try {
			o = em.merge(o);
			em.refresh(o);
			em.remove(o);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int deletarDocumento(Documento documento) {

		String q = "DELETE Documento D WHERE D.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", documento.getId());
		int result = query.executeUpdate();
		return result;

	}

	public int deletarRemuneracao(Remuneracao r) {
		String q = "DELETE Remuneracao R WHERE R.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", r.getId());
		int result = query.executeUpdate();
		return result;
	}

	public int deletarJornada(Jornada j) {
		String q = "DELETE Jornada J WHERE J.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", j.getId());
		int result = query.executeUpdate();
		return result;
	}

	public int deletarSupervisor(Supervisor s) {
		String q = "DELETE Supervisor S WHERE S.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", s.getId());
		int result = query.executeUpdate();
		return result;
	}

	public int deletarNotificacao(Notificacao n) {
		String q = "DELETE Notificacao N WHERE N.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", n.getId());
		int result = query.executeUpdate();
		return result;
	}
}