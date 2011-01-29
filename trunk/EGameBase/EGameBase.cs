
using Bitverse.Unity.Gui;
using SmartGame;
using UnityEngine;
using SmartFoxClientAPI;
using System.Collections;
using System.Collections.Generic;
using SmartGUI;
using SmartCommon;
using SmartNetworkController;
using SmartTransition;
using System;
using System.Timers;
using LitJson;


public abstract class EGameBase : BaseBehaviour {
	public static SmartTransition.levelTransition transitionController;
	
	[SerializeField]
	public NetworkController networkController;	
	
	public TaskQueue beginning;
	public TaskQueue levelEnding;
	public TaskQueue gameFinished ;
	public ScorePanel score;
	
	//protected int levelProgress = 0;
	protected const int TOTAL_SCORE_PER_LEVEL = 1000;
	
	protected float costTimePercentage = 0.5f;
	protected int degree = 1;
	private int level = 1;
	protected int Level{
		get {return level;}
		set {this.level = value;}
	}
	protected int totalRoundPerLevel = 10;
	protected int passCondition = 6;
	protected int success = 0;
	protected int round = 0;
	
	protected int scoreInLevel = 0;
	protected SmartNetworkController.NetworkController network;
    //private bool playable = false;

	protected EGameUIManager UI;
	private EgameMessage msgWindow;
	private const string ALWAYS_SHOW_HELP = "ALWAYS_SHOW_HELP";
	private DateTime levelTime = DateTime.Now;
	private bool showLevel = false;
	private string handler_name;
	

	/**
		@param
		totalRoundPerLevel: 当前等级总回合数，默认10
		passCondition: 通过该等级需要答对的次数，默认6
		costTimePercentage: 耗用时间跟最后得分所占百分比，默许0.5f (即50%)
	*/
	protected void Init(int totalRoundPerLevel, int passCondition, float costTimePercentage){
		this.totalRoundPerLevel = totalRoundPerLevel;
		this.passCondition = passCondition;
		this.costTimePercentage = costTimePercentage;
	}
	
	void Awake(){
		UI = FindObjectOfType(typeof(EGameUIManager)) as EGameUIManager;
		if(UI == null){
			Debug.LogError("Can't find minigame UI");
			return;
		}
		
		
	}
	
	protected virtual void BeforeStart() {}
	// Use this for initialization
	void Start () {
		BeforeStart();
		this.network = NetworkController.Get();
		
		this.handler_name = Common.HANDLER_LOG + "_" + this.GetSceneName();
		if (network != null)
		{
			network.Register(this.handler_name, this);
		}
		msgWindow = UI.GetMessageWindow();
		if(msgWindow == null){
			Debug.LogError("Can't find msgWindow");
			return;
		}

		
		
		UI.OnTimeOver += delegate(){
			OnTimeOver();
		};
		
		UI.OnPlay += delegate(){
			OnPlay();
		};		
	
		UI.OnExit += delegate(){
			OnExit();
		};
		OnStart();
		
	
		bool showHelp = !PlayerPrefs.HasKey(ALWAYS_SHOW_HELP) || (PlayerPrefs.GetInt(ALWAYS_SHOW_HELP) == 1);
		
		if(showHelp){		
			UI.OnHelpClosed += delegate(){
				_startBeginningTaskQueue();
			};
			UI.ShowHelp();
		}else{
			_startBeginningTaskQueue();
		}
	}
	
	private void _startBeginningTaskQueue(){
		if (this.beginning != null && this.beginning.Size > 0){
			this.beginning.OnComplete += new TaskQueue.Complete(this._afterStart);
			this.beginning.Process();
		}else{
			this._afterStart();
		}
	}
	
	private void _afterStart(){
		if (this.beginning != null) {
			this.beginning.Reset();
		}

		if (!this.IsFirstVisit()) {
			UI.ShowDemoBtnGroup(
				delegate(){ this.ShowDemo(this.OnDemo); }, 
				delegate(){
					this.OnDemo(null, true);
				}
			);
		}else{
			this.ShowDemo(this.OnDemo);
		}
		
		GameObject npcGroup = GameObject.Find("/FlyingNPC");
		if (npcGroup != null){
			int npcCount = npcGroup.transform.childCount;
			for (int idx = 0; idx < npcCount; idx++)	{
				GameObject npc = GameObject.Find("/FlyingNPC/" + idx.ToString() + "/npc");
				Vector3 from = GameObject.Find("/FlyingNPC/" + idx.ToString() + "/npcFrom").transform.position;
				Vector3 dest = GameObject.Find("/FlyingNPC/" + idx.ToString() + "/npcDest").transform.position;

				FlyObject flyingNPC = npc.GetComponent<FlyObject>() as FlyObject;
				flyingNPC.FromPos = from;
				flyingNPC.DestPos = dest;
				flyingNPC.OnTaskComplete += delegate(Task task){
					FlyObject thisObj = task as FlyObject;
					thisObj.ResetEvent();
					thisObj.Speed /= 3;
					Vector3 v1 = dest;
					Vector3 v2 = Vector3.Lerp(dest, from, 0.05f);
					int n = 0;
					thisObj.FromPos = v1;
					thisObj.DestPos = v2;
					thisObj.OnTaskComplete += delegate(Task t)	{
						FlyObject f = t as FlyObject;
						if (n <= 2)	{
							f.FromPos = (n % 2 == 0) ? v2 : v1;
							f.DestPos = (n % 2 == 0) ? v1 : v2;
							n++;
							f.StartTask();
						}
						else{
							f.ResetEvent();
							GameObject.Destroy(f);
						}
					};
					thisObj.StartTask();
				};
				flyingNPC.StartTask();
			}
		}
			
	}
	
	private void Exit(SmartTransition.levelTransition transition, string spawnPosition, string scene)   {
		SmartCommon.Common.spawnPosition = spawnPosition;
		UI.HideAll();
		transition.doTransition(scene, this);
	}
	
	protected virtual void OnDemo(GameDemo thisDemo, bool complete)	{
		if (complete) {			
			this.Reset(true);
		}else{
			thisDemo.demoLevel ++;
			thisDemo.StartDemo();
		}
	}
	
	protected virtual void OnCommit(int level, int complete, int addMoney){
		string passLevelText = "";
		if(complete > 0){
			passLevelText = "恭喜你通关！！在这一关中，你得到了" + addMoney +"个金币。";
		}else{
			passLevelText = "由于错误次数过多，因此你没有通过这一关。再加把劲试试吧！";
		}
		msgWindow.ShowMessage(null, passLevelText, delegate(){
			if(complete>0){
				this.level++;
			}
			this.OnNextLevel(complete>0);
			
			if(this.level > 3){
				if(gameFinished != null){
					gameFinished.OnComplete += delegate(){
						OnExit();
					};
						
					gameFinished.Process();
				}else{
					OnExit();
				}
			}else{
				if(levelEnding != null){
					levelEnding.Process();
				}
			}
		});
	}
	
	
		
	//
	protected virtual void OnAction(bool isRight){		
		round ++ ;
		Debug.Log(round);
		UpdateProgress((int)(1/(float)totalRoundPerLevel*100));
		if(isRight){
			success++;
			int addScore = (int)((UI.GetTickValue()*costTimePercentage + (1-costTimePercentage))*TOTAL_SCORE_PER_LEVEL/totalRoundPerLevel);
			
			AddScore(addScore);
			scoreInLevel += addScore;
		}
		if(round == totalRoundPerLevel){
			//add score, the final score depends on the costed time and basic score
			Debug.Log("LogOnServer");
			LogOnServer((success==round)?2:(success>=passCondition?1:0), success, totalRoundPerLevel);			
			
		}else{
			OnPlay();
		}
	}
		
	
	
	protected virtual string GetNextLevel(){
		return this.GetSceneName();
	}
		
	protected void AddScore(int addValue){
		this.scoreInLevel += addValue;
		this.score.AddValue(addValue);
	}	
       
	private bool IsFirstVisit()
        {
            bool ret = true;
            if (SmartCommon.SmartFox.Connection != null)
            {
                int currentRoom = SmartCommon.SmartFox.Connection.activeRoomId;
                if (SmartCommon.Common.visitedRoom.Count > 0)
                {
                    for (int i = 0; i < SmartCommon.Common.visitedRoom.Count; i++)
                    {
                        int roomId = (int) SmartCommon.Common.visitedRoom[i];
                        if (roomId == currentRoom)
                        {
                            ret = false;
                            break;
                        }
                    }
                }
                if (ret)
                {
                    SmartCommon.Common.visitedRoom.Add(currentRoom);
                }
            }
            return ret;
        }
		
		
	public void PlayAudio(string name)	{
		string path = "/Sounds/" + name;
		GameObject audioObject = GameObject.Find(name);
		if (audioObject != null)		{
			AudioSource source = audioObject.GetComponent<AudioSource>() as AudioSource;
			if (source != null){
				source.Play();
			}
		}
	}
	
	protected void NextLevel(SmartTransition.levelTransition transition) {		
			this._nextLevel(transition);	
	}
	
	private void _nextLevel(SmartTransition.levelTransition transition) {
		//this._reset();
		if (transition != null)
		{
			transition.doTransition(this.GetNextLevel(), this);
		}
		else
		{			
			this.levelTime = DateTime.Now.AddSeconds(2);
			this.showLevel = true;
		}
	}
	
	private void LogOnServer(int complete, int correct, int total)
	{
		this.commit(complete, correct, total);
	}
		
	protected void Reset(bool playable) {
		success = 0;
		round = 0;
		this.degree = 1;
		this.score.SetValue(0);
		UI.ResetProgress();
		UI.Playable = playable;				
	}
	
	private void UpdateProgress(int newValue){
		UI.UpdateProgress(newValue);
	}
			
	public void OnConnectionLost(){
		Debug.Log("Connection lost");
	}
	
	protected void OnExit(){
		UI.HideAll();
		string[] exitStr = GetExitPoint();
		networkController.QuitRoom();
		Exit(levelTransition.transitionController, exitStr[1], exitStr[0]);
	}
	
	private void _onCommit(JsonData data)
        {
			Debug.Log(data.ToJson());
            int addMoney = (int)data[Common.FIELD_GENERAL_DATA];	
            int level = (int)data[Common.FIELD_LOG_GAME_LEVEL];
            int complete = (int)data[Common.FIELD_LOG_GAME_COMPLETE];

            SmartCommon.Common.totalMoney = (int)data[Common.FIELD_GENERAL_MONEY];
            this.OnCommit(level, complete, addMoney);
        }
		
	protected override string getHandlerName()
        {
            return this.handler_name;
        }
		
	private void commit(int complete, int correct, int total)
        {
            Hashtable param = new Hashtable();
            param[SmartCommon.Common.FIELD_CLIENT_HANDLER] = this.handler_name;
            param[SmartCommon.Common.FIELD_LOG_GAME_LEVEL] = this.level;
            param[SmartCommon.Common.FIELD_LOG_GAME_COMPLETE] = complete;
            param[SmartCommon.Common.FIELD_LOG_GAME_SCORE] = this.scoreInLevel;
            param[SmartCommon.Common.FIELD_LOG_GAME_CORRECT] = correct;
            param[SmartCommon.Common.FIELD_LOG_GAME_TOTAL] = total;

            if (network != null && SmartCommon.SmartFox.Connection != null)
            {
				Debug.Log("~~~network.SendXtMessage()~~~~");
                 network.SendXtMessage(SmartCommon.SmartFox.Connection, this, SmartCommon.Common.EXTENSION_LOG, SmartCommon.Common.CMD_LOG_GAME, "_onCommit", param);
            }
            this.scoreInLevel = 0;
        }
		
	protected void InitAndStartTick(int tickTime){
		UI.TickVisiable = true;
		UI.TickInit(tickTime);
		UI.ResetTick(true);
	}
	
	protected void PauseTick(){
		UI.PauseTick();
	}
	
	private Timer timer;
	private void loadLevelCallback(string nextLevel){
		if (nextLevel != this.GetSceneName()) {
			Application.LoadLevel(nextLevel);
		}
		else
		{
			this.showLevel = true;
			int interval = 0;
			this.timer = new Timer(Common.SECOND * 1);
			this.timer.Elapsed += delegate(object source, ElapsedEventArgs e)
			{
				if (interval++ == 2)
				{
					this.showLevel = false;
					this.OnNextLevel(true);                        
				}
			};
			this.timer.Enabled = true;
		}
	}
	//====================
	private SmartFoxClient GetClient(){
            return SmartCommon.SmartFox.Connection;
        }
	//============================
	

	//game initialization
	protected abstract void OnStart();
	//Get Exit Point
	protected abstract string[] GetExitPoint();
	//Show Demo
	protected abstract void ShowDemo(GameDemo.OnDemoDelegate onDemo);
	//playing game
	protected abstract void OnPlay();
	//game level up
	protected abstract void OnNextLevel(bool pass);		
	//time over
	protected abstract void OnTimeOver();
	//Get Scene Name
	protected abstract string GetSceneName();
}
