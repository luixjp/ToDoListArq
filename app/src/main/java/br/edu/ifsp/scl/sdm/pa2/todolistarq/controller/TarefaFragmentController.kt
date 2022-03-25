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
import br.edu.ifsp.scl.sdm.pa2.todolistarq.view.TarefaFragment

class TarefaFragmentController(private val tarefaFragment: TarefaFragment) {


    /* BroadcastReceiver que recebe o ATUALIZAR TAREFA do serviço */
    private val receiveDBManagerAtualizarTarefa: BroadcastReceiver by lazy {
        object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val result = intent?.getParcelableExtra<Tarefa>(DBManagerStartedService.EXTRA_DBM) as Tarefa

                tarefaFragment.retornaTarefa(result)

                tarefaFragment.context?.applicationContext?.unregisterReceiver(receiveDBManagerAtualizarTarefa)
            }
        }
    }

    /* BroadcastReceiver que recebe o INSERIR TAREFA do serviço */
    private val receiveDBManagerInserirTarefa: BroadcastReceiver by lazy {
        object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val result = intent?.getParcelableExtra<Tarefa>(DBManagerStartedService.EXTRA_DBM) as Tarefa
                tarefaFragment.retornaTarefa(result)
                tarefaFragment.context?.applicationContext?.unregisterReceiver(receiveDBManagerInserirTarefa)
            }
        }
    }

    fun atualizaTarefa(tarefa: Tarefa) {

        tarefaFragment.context?.applicationContext?.registerReceiver(receiveDBManagerAtualizarTarefa, IntentFilter(DBManagerStartedService.ACTION_RECEIVE_ATUALIZAR_TAREFA))

        val intent = Intent(tarefaFragment.context, DBManagerStartedService::class.java)
        intent.putExtra(DBManagerStartedService.OPERATION, 3)
        intent.putExtra(DBManagerStartedService.TAREFA, tarefa)
        tarefaFragment.context?.applicationContext?.startService(intent)
    }

    fun insereTarefa(tarefa: Tarefa) {

        tarefaFragment.context?.applicationContext?.registerReceiver(receiveDBManagerInserirTarefa, IntentFilter(DBManagerStartedService.ACTION_RECEIVE_INSERIR_TAREFA))

        val intent = Intent(tarefaFragment.context, DBManagerStartedService::class.java)
        intent.putExtra(DBManagerStartedService.OPERATION, 4)
        intent.putExtra(DBManagerStartedService.TAREFA, tarefa)
        tarefaFragment.context?.applicationContext?.startService(intent)
    }
}