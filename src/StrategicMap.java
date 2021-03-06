import java.util.Random;

import lenz.htw.zpifub.net.NetworkClient;

public class StrategicMap {

	MapFrame mapframe;
	NetworkClient client;
	private BoardConfig config;

	public StrategicMap(NetworkClient client, BoardConfig config, boolean display) {
		this.client = client;
		mapframe = new MapFrame(Util.BOARD_SIZE);		
		mapframe.setVisible(display);
		this.config = config;
		config.layer = new int[10][][];
		config.walklayer = new int[10][][];
		config.debuglayer = new int[10][][];
		config.scoreHeatmap = new int[10][][];
		int size  = Util.BOARD_SIZE;

		for(int i = 0; i < config.layer.length; i++){
			config.layer[i] = new int[size][size];
			config.walklayer[i] = new int[size][size];
			config.debuglayer[i] = new int[size][size];
			config.scoreHeatmap[i] = new int[size][size];
			size = size / 2;
		}
	}
	

	//int[][][] layer;

	int x = 0;
	int y = 0;
	int chunkSize = 0;
	public void pullChunk(){
		pullChunkFromServer(x,y,chunkSize);
		x+=chunkSize;
		if(x>=Util.BOARD_SIZE){
			x= 0;
			y+=chunkSize;
			if(y>=Util.BOARD_SIZE) y= 0;
		}
	}
	
	public void pullChunkFromServer(int startX, int startY, int size) {
		int endX = startX + size;
		int endY = startY + size;
		for(int x = startX; x<endX; x++){
			for(int y = startY; y<endY; y++){
			    	boolean isWithinSlowDownArea = config.isWithinSlowPowerupArea(x ,y);
				config.layer[0][x][y] = isWithinSlowDownArea ? 0 : client.getBoard(x, y);					
			}	
		}
		
	}
	
	public void pullCompleteMap(){
		refreshLayer(0,Util.BOARD_SIZE,0,0);
	}
	
	public void update(int x,int y ,int minSize){
		update2(x,y,0,0,1024,9,minSize);
		xX+=32;

//		if(xX%1024==0) yY+=32;
//		System.out.println(xX+":"+yY);

	}
	int xX = 0;
	int yY = 0;
	Random rand = new Random();
	
	private void update2(int xIn, int yIn,int xOffset,int yOffset, int size, int layer, int minsize){
		
		if(xIn < (size/2)+xOffset && yIn < (size/2)+yOffset ) {
			//TopLeft

			if(size>minsize) {
				update2(xIn,yIn,xOffset,yOffset,size/2,layer-1,minsize);
				refreshLayer(layer,1024>>layer,0,0);
			}
			else {
				//refreshLayer(0,size,xOffset,yOffset);

				for(int l = 1; l<layer+1; l++ ) {
					refreshLayer(l,size>>(l-1),xOffset>>(l-1),yOffset>>(l-1));
				}
//				return;
			}
		}		
		else if(xIn >= (size/2)+xOffset && yIn < (size/2)+yOffset ){
			
			//TOP Right
			if(size>minsize){
				update2(xIn,yIn,xOffset+size/2,yOffset,size/2,layer-1,minsize);
				refreshLayer(layer,1024>>layer-1,0,0);
//				refreshLayer(layer,size,0,0);
//
			}
			else {
				//refreshLayer(0,size,xOffset,yOffset);
//				refreshLayer(0,size,size,0);
				for(int l = 1; l<layer+1; l++ ) {
					refreshLayer(l,size>>(l-1),xOffset>>(l-1),yOffset>>(l-1));
				}
//				return;
			}
		}
		
		else if(xIn < (size/2)+xOffset && yIn >= (size/2)+yOffset) {
			//Bottom left
			if(size>minsize){
				update2(xIn,yIn,xOffset,yOffset+size/2,size/2,layer-1,minsize);
				refreshLayer(layer,1024>>layer-1,0,0);
			} else {
				//refreshLayer(0,size,xOffset,yOffset);
				for(int l = 1; l<layer+1; l++ ) {
					refreshLayer(l,size>>(l-1),xOffset>>(l-1),yOffset>>(l-1));
				}
			}
		} else{
			//Bottom right
			if(size>minsize){
				update2(xIn,yIn,xOffset+size/2,yOffset+size/2,size/2,layer-1,minsize);
				refreshLayer(layer,1024>>layer-1,0,0);
			} else {
				//refreshLayer(0,size,xOffset,yOffset);
				for(int l = 1; l<layer+1; l++ ) {
					refreshLayer(l,size>>(l-1),xOffset>>(l-1),yOffset>>(l-1));
				}
			}
		}		
	}
	
	public void initWalkMap() {
		int size = 1024;
		for(int x = 0; x<size; x++){
			for(int y = 0; y<size; y++){
				config.walklayer[0][x][y] = client.getBoard(x, y);				
			}	
		}
		
		int xOffset = 0;
		int yOffset = 0;

		for(int layerN = 1; layerN < 10 ; layerN++){
			int l = layerN-1;
			for(int x = 0; x < size; x+=2){
				for(int y = 0; y < size ; y+=2){					
					if(config.walklayer[l][x][y] == 0) {
						continue;
					}
					if(config.walklayer[l][x+1][y] == 0) {
						continue;
					}
					if(config.walklayer[l][x][y+1] == 0) {
						continue;
					}
					if(config.walklayer[l][x+1][y+1] == 0) {
						continue;
					}
					
					config.walklayer[layerN][(x+xOffset)>>1][(y+yOffset)>>1] = Integer.MAX_VALUE;
				}	
			}
			size/=2;
		}
	}
	
	public void refreshLayer(int layerN,int size, int xOffset, int yOffset){
		if(layerN == 0){
			for(int x = 0; x<size; x++){
				for(int y = 0; y<size; y++){
				    	boolean isWithinSlowDownArea = config.isWithinSlowPowerupArea(x + xOffset,y + yOffset);
					config.layer[0][x+xOffset][y+yOffset] = isWithinSlowDownArea ? 0 : client.getBoard(x+xOffset, y+yOffset);					
				}	
			}
		} else {
			int l = layerN - 1;
			for(int x = 0; x < size; x+=2){
				for(int y = 0; y < size ; y+=2){
					int xIn = x+xOffset;
					int yIn = y+yOffset;
					int r =(((config.layer[l][xIn    ][yIn    ] >> 16) & 0x000000ff) + 
							((config.layer[l][xIn + 1][yIn    ] >> 16) & 0x000000ff) + 
							((config.layer[l][xIn    ][yIn + 1] >> 16) & 0x000000ff) + 
							((config.layer[l][xIn + 1][yIn + 1] >> 16) & 0x000000ff)) >> 2;
					
					int g =(((config.layer[l][xIn    ][yIn    ] >> 8 ) & 0x000000ff) + 
							((config.layer[l][xIn + 1][yIn    ] >> 8 ) & 0x000000ff) + 
							((config.layer[l][xIn    ][yIn + 1] >> 8 ) & 0x000000ff) + 
							((config.layer[l][xIn + 1][yIn + 1] >> 8 ) & 0x000000ff)) >> 2;
							
					int b =(((config.layer[l][xIn    ][yIn    ] >> 0 ) & 0x000000ff) + 
							((config.layer[l][xIn + 1][yIn    ] >> 0 ) & 0x000000ff) + 
							((config.layer[l][xIn    ][yIn + 1] >> 0 ) & 0x000000ff) + 
							((config.layer[l][xIn + 1][yIn + 1] >> 0 ) & 0x000000ff)) >> 2;		
					
							config.layer[layerN][(x+xOffset)>>1][(y+yOffset)>>1] = (r <<16 | g << 8 | b);
				}	
			}
		}
	}
	
	public void renderWalkMap(){
//		//update(xXx,yYy,warumGehtDieSchei�eNichtH�����);		
//		int offsetX = 0;
//		int offsetY = 0;
//		int size = 1;
//		int offsetIncrease = 1024;		
//		for(int l = 0; l < 10; l++) {
//			if(l<5) {
//				for(int y = 0; y < config.walklayer[l].length; y+=size){
//					for(int x = 0; x < config.walklayer[l].length; x += size){			
//						mapframe.setPixel((x/size) + offsetX, (y/size) + offsetY, config.walklayer[l][x][y]);
//					}
//				}
//				//size = size << 1;
//				if(l % 2 == 0) offsetX += offsetIncrease;
//				else offsetY += offsetIncrease;
//				offsetIncrease = offsetIncrease >> 1;
//			} else {
//				offsetX = 1600;
//				if(l==5 )offsetY = 0;
//				else offsetY = (l-4)*140+50;
//				for(int y = 0; y < config.walklayer[l].length*8; y++){
//					for(int x = 0; x < config.walklayer[l].length*8; x ++){			
//						mapframe.setPixel((x) + offsetX, (y) + offsetY, config.walklayer[l][x/8][y/8]);
//					}
//				}
//			}
//		}
//		mapframe.invalidate();
	}
	
	public void renderDebugMap(){
//		//update(xXx,yYy,warumGehtDieSchei�eNichtH�����);		
//		int offsetX = 0;
//		int offsetY = 0;
//		int size = 1;
//		int offsetIncrease = 1024;		
//		for(int l = 0; l < 10; l++) {
//			if(l<5) {
//				for(int y = 0; y < config.debuglayer[l].length; y+=size){
//					for(int x = 0; x < config.debuglayer[l].length; x += size){			
//						mapframe.setPixel((x/size) + offsetX, (y/size) + offsetY, config.debuglayer[l][x][y]);
//					}
//				}
//				//size = size << 1;
//				if(l % 2 == 0) offsetX += offsetIncrease;
//				else offsetY += offsetIncrease;
//				offsetIncrease = offsetIncrease >> 1;
//			} else {
//				offsetX = 1600;
//				if(l==5 )offsetY = 0;
//				else offsetY = (l-4)*140+50;
//				for(int y = 0; y < config.debuglayer[l].length*8; y++){
//					for(int x = 0; x < config.debuglayer[l].length*8; x ++){			
//						mapframe.setPixel((x) + offsetX, (y) + offsetY, config.debuglayer[l][x/8][y/8]);
//					}
//				}
//			}
//		}
//		mapframe.invalidate();
	}
	
	public void render() {		
//		int offsetX = 0;
//		int offsetY = 0;
//		int size = 1;
//		int offsetIncrease = 1024;		
//		for(int l = 0; l < 10; l++) {
//			if(l<5) {
//				for(int y = 0; y < config.scoreHeatmap[l].length; y+=size){
//					for(int x = 0; x < config.scoreHeatmap[l].length; x += size){			
//						mapframe.setPixel((x/size) + offsetX, (y/size) + offsetY, config.scoreHeatmap[l][x][y]);
//					}
//				}
//				//size = size << 1;
//				if(l % 2 == 0) offsetX += offsetIncrease;
//				else offsetY += offsetIncrease;
//				offsetIncrease = offsetIncrease >> 1;
//			} else {
//				offsetX = 1600;
//				if(l==5 )offsetY = 0;
//				else offsetY = (l-4)*140+50;
//				for(int y = 0; y < config.scoreHeatmap[l].length*8; y++){
//					for(int x = 0; x < config.scoreHeatmap[l].length*8; x ++){			
//						mapframe.setPixel((x) + offsetX, (y) + offsetY, config.scoreHeatmap[l][x/8][y/8]);
//					}
//				}
//			}
//		}
//		mapframe.invalidate();
	}
}
