package br.edu.ifsp.scl.sdm.pa2.todolistarq.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import androidx.room.Room
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.database.ToDoListArqDatabase
import br.edu.ifsp.scl.sdm.pa2.todolistarq.model.entity.Tarefa
import br.edu.ifsp.scl.sdm.pa2.todolistarq.service.DBManagerStartedService
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.ListaTarefasFragment

class ListaTarefasController(private val listaTarefaFragment : ListaTarefasFragment) {

    /* BroadcastReceiver que recebe o BUSCAR TAREFA do serviço */
    private val receiveDBManagerBuscarTarefas: BroadcastReceiver by lazy {
        object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val result = intent?.getParcelableArrayListExtra<Tarefa>(DBManagerStartedService.EXTRA_DBM) as ArrayList<Tarefa>
                val listaTarefas = mutableListOf<Tarefa>()
                result?.forEach{ tarefa ->
                    listaTarefas.add(tarefa as Tarefa)
                }
                listaTarefaFragment.atualizarListaTarefas(listaTarefas)

                listaTarefaFragment.context?.applicationContext?.unregisterReceiver(receiveDBManagerBuscarTarefas)
            }
        }
    }


    fun buscarTarefas() {

        listaTarefaFragment.context?.applicationContext?.registerReceiver(receiveDBManagerBuscarTarefas, IntentFilter(DBManagerStartedService.ACTION_RECEIVE_BUSCAR_TAREFAS))

        val intent = Intent(listaTarefaFragment.context, DBManagerStartedService::class.java)
        intent.putExtra(DBManagerStartedService.OPERATION, 1)
        listaTarefaFragment.context?.applicationContext?.startService(intent)
    }

    fun removerTarefa(tarefa: Tarefa) {

        val intent = Intent(listaTarefaFragment.context, DBManagerStartedService::class.java)
        intent.putExtra(DBManagerStartedService.OPERATION, 2)
        intent.putExtra(DBManagerStartedService.TAREFA, tarefa)
        listaTarefaFragment.context?.applicationContext?.startService(intent)
    }
}