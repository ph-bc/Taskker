package modelo.entidades;

import java.io.Serializable;
import java.util.Date;

public class Tarefa implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer cod;
	private String nome;
	private Date dataCriacao;
	
	private Login login;
	
	public Tarefa() {}

	public Tarefa(Integer cod, String nome, Date dataCriacao, Login login) {
		this.cod = cod;
		this.nome = nome;
		this.dataCriacao = dataCriacao;
		this.login = login;
	}

	public Integer getCod() {
		return cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cod == null) ? 0 : cod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tarefa other = (Tarefa) obj;
		if (cod == null) {
			if (other.cod != null)
				return false;
		} else if (!cod.equals(other.cod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tarefa [cod=" + cod + ", nome=" + nome + ", dataCriacao=" + dataCriacao + ", login=" + login + "]";
	}
}
