package br.edu.ifsp.scl.sdm.pa2.todolistarq.controller

import android.os.AsyncTask
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.ListaTarefasFragment

class ListaTarefasController(private val listaTarefaFragment : ListaTarefasFragment) {
    private val database: ToDoListArqDatabase
    init {
        database = Room.databaseBuilder(
            listaTarefaFragment.requireContext(),
            ToDoListArqDatabase::class.java,
            ToDoListArqDatabase.Constantes.DB_NAME
        ).build()
    }

    fun buscarTarefas() {
        object : AsyncTask<Unit, Unit, List<Tarefa>>(){
            override fun doInBackground(vararg params: Unit?): List<Tarefa> {
                return database.getTarefaDao().recuperarTarefas()
            }

            override fun onPostExecute(result: List<Tarefa>?) {
                super.onPostExecute(result)
                val listaTarefas = mutableListOf<Tarefa>()
                result?.forEach{ tarefa ->
                    listaTarefas.add(tarefa)
                }
                listaTarefaFragment.atualizarListaTarefas(listaTarefas)
            }

        }.execute()
    }

    fun removerTarefa(tarefa: Tarefa) {
        object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?): Unit {
                return database.getTarefaDao().removerTarefa(tarefa)
            }

        }.execute()
    }
}