package modelo.entidades;

import java.io.Serializable;
import java.util.Date;

import modelo.enums.Situacao;

public class SubTarefa implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer cod;
	private String nome;
	private String descricao;
	private Date dataCriacao;
	private Date dataPrazo;
	
	private Situacao status;
	
	private Tarefa tarefa;
	
	public SubTarefa() {}

	public SubTarefa(Integer cod, String nome, String descricao, Date dataCriacao, Date dataPrazo, Situacao status,
			Tarefa tarefa) {
		this.cod = cod;
		this.nome = nome;
		this.descricao = descricao;
		this.dataCriacao = dataCriacao;
		this.dataPrazo = dataPrazo;
		this.status = status;
		this.tarefa = tarefa;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataPrazo() {
		return dataPrazo;
	}

	public void setDataPrazo(Date dataPrazo) {
		this.dataPrazo = dataPrazo;
	}

	public Situacao getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = Situacao.valueOf(status);
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
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
		SubTarefa other = (SubTarefa) obj;
		if (cod == null) {
			if (other.cod != null)
				return false;
		} else if (!cod.equals(other.cod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubTarefa [cod=" + cod + ", nome=" + nome + ", descricao=" + descricao + ", dataCriacao=" + dataCriacao
				+ ", dataPrazo=" + dataPrazo + ", status=" + status + ", tarefa=" + tarefa + "]";
	}
}
