import express from 'express'
const app=express(); app.use(express.json());
app.post('/agent/plan',async(req,res)=>{ if(!process.env.GEMINI_API_KEY)return res.status(503).json({error:'Backend AI provider is not configured'}); res.json({goal:req.body.command,actions:[{type:'OBSERVE',id:'observe'}]}) })
app.post('/vision/analyze',async(_req,res)=>res.json({description:'Vision endpoint placeholder contract; provider implementation belongs here.'}))
app.listen(process.env.PORT||8787)
