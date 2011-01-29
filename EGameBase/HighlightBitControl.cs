using UnityEngine;
using System.Collections;

public class HighlightBitControl : MonoBehaviour {
	public GUIStyle highlightStyle;
	
	public Vector2 offset = new Vector2(10f, 10f);
	public bool show = false;
	public float blinkInterval = 1f;
	public int blinkCount = -1;
	
	private Rect position;
	private float delta = 0;
	
	void Start(){		
		position = gameObject.GetComponent<BitControl>().AbsolutePosition;
	}
	
	public void Init(float interval, int count, Texture2D border){
		this.blinkInterval = interval;
		this.blinkCount = count;
		highlightStyle.normal.background = border;
	}
	
	void OnGUI () {		
		if(show){
			delta += Time.deltaTime;			
			if(delta > blinkInterval && (blinkCount!=0 || blinkCount == -1 )){				
				GUI.Label(new Rect(position.x-offset.x, position.y-offset.y, position.width+2*offset.x, position.height+2*offset.y), "", highlightStyle);
				
				if(delta > 2*blinkInterval){
					delta = 0;
					if(blinkCount > 0){
						blinkCount--;
					}
				}
			}			
		}
	}
}
