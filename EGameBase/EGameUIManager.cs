using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using Bitverse.Unity.Gui;
using LitJson;
using SmartTask;

public class EGameUIManager : MonoBehaviour {
	public Texture[] helpPictures;
	
	public bool Playable{
		set {startButton.Visible = value;}
	}
	
	public BitWindow GetGameUIWindow(){
		return gameWindow;
	}
	public EgameMessage GetMessageWindow(){
		return root.FindControl<BitWindow>("EGameMessageWindow").gameObject.GetComponent<EgameMessage>();
	}
	#region Progress
	public void UpdateProgress(int inc){
		progressBar.Value += inc;
	}
	
	public void ResetProgress(){
		progressBar.Value = 0;
	}
	#endregion 
	
	public void ShowHelp(){
		gameHelpWindow.Visible = true;
		gameControlWindow.Visible = false;
	}
	
	public void SetConversationText(string text){
		conversation.Content.text = text;
	}
	
	#region tick counter
	public void TickInit(float seconds){
		this.tickSeconds = seconds;	
	}		
	
	public float GetTickValue() {
		return tickCounter.Value / (tickCounter.MaxValue - tickCounter.MinValue);
	}
	
	public void ResetTick(bool restartTick){
		tickCounter.Value = tickCounter.MaxValue - tickCounter.MinValue;
		tickCurrent = 0;
		this.tickStartFlag = restartTick;
	}
	
	public bool TickVisiable{
		set { tickCounter.Visible = value;}
	}
	
	public void PauseTick(){
		this.tickStartFlag = false;
	}
	#endregion
	
	
	#region demo
	public void ShowDemoBtnGroup(OnPlayDemoDelegate playDemo, OnSkipDemoDelegate skipDemo){
		skipDemoBtn.Visible = true;
		playDemoBtn.Visible = true;
		skipDemoBtn.MouseClick += delegate(object sender, MouseEventArgs e){
			skipDemo();
			skipDemoBtn.Visible = false;
			playDemoBtn.Visible = false;
		};
		
		playDemoBtn.MouseClick += delegate(object sender, MouseEventArgs e){
			playDemo();
			skipDemoBtn.Visible = false;
			playDemoBtn.Visible = false;
		};
	}
	#endregion
	
	
	public void HideAll(){
		root.Visible = false;
	}
	#region delegates
	public delegate void OnSkipDemoDelegate();
	
	public delegate void OnPlayDemoDelegate();
	
	public delegate void OnTimeOverDelegate();
	public event OnTimeOverDelegate OnTimeOver;	
	
	public delegate void OnPlayDelegate();
	public event OnPlayDelegate OnPlay;
	
	public delegate void OnExitDelegate();
	public event OnExitDelegate OnExit;
	
	public delegate void OnHelpClosedDelegate();
	public event OnHelpClosedDelegate OnHelpClosed;
	#endregion
	
	
	private BitStage root;
	
	private BitWindow gameControlWindow;
	private BitButton startButton, helpButton, exitButton;
	private BitHorizontalProgressBar progressBar;
	private BitVerticalProgressBar tickCounter;
	private BitGroup centerGroup;
	private BitButton skipDemoBtn, playDemoBtn;
	private BitBox conversation;
	
	private BitWindow gameHelpWindow;
	private BitButton hPreBtn, hNextBtn, hCloseBtn;
	private BitPicture hPictureRender;
	private BitToggle hAlwaysShowHelp;
	
	private BitWindow gameWindow;
	
	void Awake(){
		root = GetComponent<BitStage>();
		
		gameControlWindow = root.FindControl<BitWindow>("EGameControlWindow");
		conversation = gameControlWindow.FindControl<BitBox>("ConversationBox");
		BitGroup headGroup = gameControlWindow.FindControl<BitGroup>("HeadGroup");
		
		helpButton = headGroup.FindControl<BitButton>("help");
		exitButton = headGroup.FindControl<BitButton>("exit");
		progressBar = headGroup.FindControl<BitGroup>("progress_bg").FindControl<BitHorizontalProgressBar>();
		tickCounter = gameControlWindow.FindControl<BitGroup>("TickCounter_bg").FindControl<BitVerticalProgressBar>();
		centerGroup = gameControlWindow.FindControl<BitGroup>("CenterGroup");
		skipDemoBtn = centerGroup.FindControl<BitButton>("SkipDemoBtn");
		playDemoBtn = centerGroup.FindControl<BitButton>("PlayDemoBtn");
		startButton = centerGroup.FindControl<BitButton>("start");
		
		gameHelpWindow = root.FindControl<BitWindow>("EGameHelpWindow");
		hPictureRender = gameHelpWindow.FindControl<BitPicture>();
		hPreBtn = gameHelpWindow.FindControl<BitButton>("help_btn_prev");
		hNextBtn = gameHelpWindow.FindControl<BitButton>("help_btn_next");
		hCloseBtn = gameHelpWindow.FindControl<BitButton>("close");
		hAlwaysShowHelp = gameHelpWindow.FindControl<BitToggle>();
		
		gameWindow = root.FindControl<BitWindow>("GameWindow");
	}

	void Start () {
		/*caculate positions of each ui component first.*/
		
		/*help window*/
		initHelpWindow();
		
		//TickInit(10);
			
		startButton.MouseClick += delegate(object sender, MouseEventArgs e){			
			if(OnPlay != null){
				OnPlay();
				startButton.Visible = false;				
			}
		};	
		
		exitButton.MouseClick += delegate(object sender, MouseEventArgs e){
			if( OnExit!= null ){
				
				OnExit();
				
			}
		};
		
	}
	
	
	private bool tickStartFlag = false;
	private float tickSeconds = 10;
	private float tickCurrent = 0;
	void Update () {
		if(tickStartFlag){
			tickCurrent += Time.deltaTime;
			if(tickCurrent < tickSeconds){
				float val = (tickSeconds-tickCurrent) * (tickCounter.MaxValue - tickCounter.MinValue)/tickSeconds;
				val = val<4 ? 0 : val;		//adjust. 
				tickCounter.Value  =  val;
			}else{
				GameObject audio = GameObject.Find("/Sounds/Timeout");
				if (audio != null)				{
					AudioSource source = audio.GetComponent<AudioSource>() as AudioSource;
					source.Play();
				}
					
				tickStartFlag = false;
				tickCurrent = 0;				
				if(OnTimeOver != null){
					
					OnTimeOver();
					
				}
			}
		}
		gameControlWindow.Depth = 0;				
	}
	
	private int curHelpPicIndex = 0;
	private void initHelpWindow(){
		hCloseBtn.MouseClick += delegate(object sender, MouseEventArgs e){			
			gameHelpWindow.Visible = false;
			gameControlWindow.Visible = true;
			if(OnHelpClosed != null){
				OnHelpClosed();
				OnHelpClosed = null;
			}
		};
		
		helpButton.MouseClick += delegate(object sender, MouseEventArgs e){
			gameHelpWindow.Visible = true;
			gameControlWindow.Visible = false;
		};
		
		if(helpPictures.Length == 0){
			helpButton.Enabled = false;
		}else if(helpPictures.Length == 1){
			hPreBtn.Visible = hNextBtn.Visible = false;
			hPictureRender.Content.image = helpPictures[0];
		}else {
			hPictureRender.Content.image = helpPictures[0];
			hPreBtn.Visible = hNextBtn.Visible = true;
			hPreBtn.Enabled = false;
			hNextBtn.Enabled = true;
			
			hPreBtn.MouseClick += delegate(object sender, MouseEventArgs e){				
				curHelpPicIndex--;
				hPictureRender.Content.image = helpPictures[curHelpPicIndex];
				hNextBtn.Enabled = true;
				hPreBtn.Enabled = curHelpPicIndex>0;				
			};
			
			hNextBtn.MouseClick += delegate(object sender, MouseEventArgs e){
				curHelpPicIndex++;
				hPictureRender.Content.image = helpPictures[curHelpPicIndex];
				hNextBtn.Enabled = curHelpPicIndex < helpPictures.Length-1;
				hPreBtn.Enabled = true;
			};			
		}		
	}
}
