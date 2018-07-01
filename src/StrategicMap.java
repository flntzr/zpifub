import java.util.Random;

import lenz.htw.zpifub.net.NetworkClient;

public class StrategicMap {

	MapFrame mapframe;
	NetworkClient client;
	public StrategicMap(NetworkClient client) {
		this.client = client;
		mapframe = new MapFrame(Util.BOARD_SIZE);		
		mapframe.setVisible(true);
		layer = new int[10][][];
		int size  = Util.BOARD_SIZE;
		for(int i = 0; i < layer.length; i++){
			layer[i] = new int[size][size];
			size = size / 2;
		}
	}
	
	int[][][] layer;
	
	public void update(int x,int y ,int minSize){

//		for(int x = 0; x<1024; x++){
//			for(int y = 0; y<1024; y++){
//				layer[0][x][y] = client.getBoard(x, y);					
//			}	
//		}
//
//		for(int l = 0; l < 9; l++){
//
//			for(int x = 0; x<layer[l].length; x+=2){
//				for(int y = 0; y<layer[l].length; y+=2){
//					int r =(((layer[l][x    ][y    ] >> 16) & 0x000000ff) + 
//							((layer[l][x + 1][y    ] >> 16) & 0x000000ff) + 
//							((layer[l][x    ][y + 1] >> 16) & 0x000000ff) + 
//							((layer[l][x + 1][y + 1] >> 16) & 0x000000ff)) >> 2;
//					
//					int g =(((layer[l][x    ][y    ] >> 8 ) & 0x000000ff) + 
//							((layer[l][x + 1][y    ] >> 8 ) & 0x000000ff) + 
//							((layer[l][x    ][y + 1] >> 8 ) & 0x000000ff) + 
//							((layer[l][x + 1][y + 1] >> 8 ) & 0x000000ff)) >> 2;
//							
//					int b =(((layer[l][x    ][y    ] >> 0 ) & 0x000000ff) + 
//							((layer[l][x + 1][y    ] >> 0 ) & 0x000000ff) + 
//							((layer[l][x    ][y + 1] >> 0 ) & 0x000000ff) + 
//							((layer[l][x + 1][y + 1] >> 0 ) & 0x000000ff)) >> 2;		
//					
//					layer[l+1][x>>1][y>>1] = (r <<16 | g << 8 | b);
//				}
//			}
//		}
		
		//update2(10,20,1024,9);
		//update2(rand.nextInt(1023),rand.nextInt(1023),0,0,1024,9,16);



		update2(x,y,0,0,1024,9,minSize);
		//update2(300,300,0,0,1024,9,16);
		xX+=32;
		if(xX%1024==0) yY+=32;
		System.out.println(xX+":"+yY);
	}
	int xX = 0;
	int yY = 0;
	Random rand = new Random();
	
	public void update2(int xIn, int yIn,int xOffset,int yOffset, int size, int layer, int minsize){
		
		if(xIn < (size/2)+xOffset && yIn < (size/2)+yOffset ) {
			//TopLeft

			if(size>minsize) {
				update2(xIn,yIn,xOffset,yOffset,size/2,layer-1,minsize);
				refreshLayer(layer,1024>>layer,0,0);
			}
			else {
				refreshLayer(0,size,xOffset,yOffset);
//				refreshLayer(0,size,0,0);
				//				refreshLayer(1,size,xOffset,yOffset);
//				refreshLayer(2,size>>1,xOffset>>1,yOffset>>1);
//				refreshLayer(1,size>>1,xOffset+16,yOffset);
				//refreshLayer(1,size,xOffset+16,yOffset);
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
				refreshLayer(0,size,xOffset,yOffset);
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
				refreshLayer(0,size,xOffset,yOffset);
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
				refreshLayer(0,size,xOffset,yOffset);
				for(int l = 1; l<layer+1; l++ ) {
					refreshLayer(l,size>>(l-1),xOffset>>(l-1),yOffset>>(l-1));
				}
			}
		}
//		refreshLayer(0,256,50,50);
//		refreshLayer(1,256,0,0);
//		refreshLayer(1,256,256,0);
//		refreshLayer(1,256,256,256);
//		refreshLayer(1,256,0,256);
//		if(size>2) update2(xIn, yIn, size >> 2);
//		if(xIn<)
//		for(int x = 0; x<size; x++){
//			for(int y = 0; y<size; y++){
//				layer[0][x][y] = client.getBoard(x, y);					
//			}	
//		}		
	}
	
	public void refreshLayer(int layerN,int size, int xOffset, int yOffset){
		if(layerN == 0){
			for(int x = 0; x<size; x++){
				for(int y = 0; y<size; y++){
					layer[0][x+xOffset][y+yOffset] = client.getBoard(x+xOffset, y+yOffset);					
				}	
			}
		} else {
			int l = layerN - 1;
			for(int x = 0; x < size; x+=2){
				for(int y = 0; y < size ; y+=2){
					int xIn = x+xOffset;
					int yIn = y+yOffset;
					int r =(((layer[l][xIn    ][yIn    ] >> 16) & 0x000000ff) + 
							((layer[l][xIn + 1][yIn    ] >> 16) & 0x000000ff) + 
							((layer[l][xIn    ][yIn + 1] >> 16) & 0x000000ff) + 
							((layer[l][xIn + 1][yIn + 1] >> 16) & 0x000000ff)) >> 2;
					
					int g =(((layer[l][xIn    ][yIn    ] >> 8 ) & 0x000000ff) + 
							((layer[l][xIn + 1][yIn    ] >> 8 ) & 0x000000ff) + 
							((layer[l][xIn    ][yIn + 1] >> 8 ) & 0x000000ff) + 
							((layer[l][xIn + 1][yIn + 1] >> 8 ) & 0x000000ff)) >> 2;
							
					int b =(((layer[l][xIn    ][yIn    ] >> 0 ) & 0x000000ff) + 
							((layer[l][xIn + 1][yIn    ] >> 0 ) & 0x000000ff) + 
							((layer[l][xIn    ][yIn + 1] >> 0 ) & 0x000000ff) + 
							((layer[l][xIn + 1][yIn + 1] >> 0 ) & 0x000000ff)) >> 2;		
					
					layer[layerN][(x+xOffset)>>1][(y+yOffset)>>1] = (r <<16 | g << 8 | b);			
				}	
			}
		}
	}
	
	public void render() {
		int offsetX = 0;
		int offsetY = 0;
		int size = 1;
		int offsetIncrease = 1024;		
		for(int l = 0; l < 10; l++) {
			if(l<5) {
				for(int y = 0; y < layer[l].length; y+=size){
					for(int x = 0; x < layer[l].length; x += size){			
						mapframe.setPixel((x/size) + offsetX, (y/size) + offsetY, layer[l][x][y]);
					}
				}
				//size = size << 1;
				if(l % 2 == 0) offsetX += offsetIncrease;
				else offsetY += offsetIncrease;
				offsetIncrease = offsetIncrease >> 1;
			} else {
				offsetX = 1600;
				if(l==5 )offsetY = 0;
				else offsetY = (l-4)*140+50;
				for(int y = 0; y < layer[l].length*8; y++){
					for(int x = 0; x < layer[l].length*8; x ++){			
						mapframe.setPixel((x) + offsetX, (y) + offsetY, layer[l][x/8][y/8]);
					}
				}
			}
		}
		mapframe.invalidate();
	}
}
